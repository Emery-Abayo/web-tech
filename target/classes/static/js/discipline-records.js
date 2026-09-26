const tableBody = document.getElementById("record-rows");
const stateBox = document.getElementById("state-box");
const banner = document.getElementById("banner");
const modalBackdrop = document.getElementById("modal-backdrop");
const modalTitle = document.getElementById("modal-title");
const form = document.getElementById("record-form");
const deleteBackdrop = document.getElementById("delete-backdrop");
const scoreStudentSelect = document.getElementById("score-student-select");
const scoreValue = document.getElementById("score-value");

let editingId = null;
let pendingDeleteId = null;
let students = [];

function showBanner(message, type) {
  banner.textContent = message;
  banner.className = "banner " + type;
  banner.style.display = "block";
  setTimeout(() => { banner.style.display = "none"; }, 4000);
}

function clearFieldErrors() {
  form.querySelectorAll(".field-error").forEach((el) => (el.textContent = ""));
}

function applyFieldErrors(fields) {
  clearFieldErrors();
  if (!fields) return;
  Object.entries(fields).forEach(([field, message]) => {
    const el = document.getElementById("err-" + field);
    if (el) el.textContent = message;
  });
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str == null ? "" : str;
  return div.innerHTML;
}

function studentLabel(s) {
  return `${s.firstName} ${s.lastName} (${s.studentNumber})`;
}

function populateStudentDropdowns() {
  const optionsHtml = students.map((s) => `<option value="${s.id}">${escapeHtml(studentLabel(s))}</option>`).join("");
  form.studentId.innerHTML = `<option value="">Select student...</option>` + optionsHtml;
  scoreStudentSelect.innerHTML = `<option value="">Select student...</option>` + optionsHtml;
}

async function loadStudentsForDropdown() {
  try {
    students = await api.get("/students");
    populateStudentDropdowns();
  } catch (err) {
    showBanner("Failed to load students for dropdown: " + err.message, "error");
  }
}

async function loadRecords() {
  stateBox.style.display = "block";
  stateBox.className = "loading-state";
  stateBox.textContent = "Loading discipline records...";
  tableBody.innerHTML = "";

  try {
    const records = await api.get("/discipline-records");
    if (records.length === 0) {
      stateBox.className = "empty-state";
      stateBox.textContent = "No discipline records yet. Click \"Add Record\" to create one.";
      return;
    }
    stateBox.style.display = "none";
    records.forEach((r) => tableBody.appendChild(renderRow(r)));
  } catch (err) {
    stateBox.className = "error-state";
    stateBox.textContent = "Failed to load discipline records: " + err.message;
  }
}

function renderRow(r) {
  const tr = document.createElement("tr");
  const studentName = r.student ? `${r.student.firstName} ${r.student.lastName}` : "(unknown)";
  tr.innerHTML = `
    <td>${r.id}</td>
    <td>${escapeHtml(studentName)}</td>
    <td>${escapeHtml(r.incidentDate)}</td>
    <td>${escapeHtml(r.fault)}</td>
    <td>${escapeHtml(r.punishment)}</td>
    <td>${r.points}</td>
    <td>${escapeHtml(r.status)}</td>
    <td class="actions-cell">
      <button class="secondary small" data-edit="${r.id}">Edit</button>
      <button class="danger small" data-delete="${r.id}">Delete</button>
    </td>
  `;
  tr.querySelector("[data-edit]").addEventListener("click", () => openEditModal(r));
  tr.querySelector("[data-delete]").addEventListener("click", () => openDeleteConfirm(r.id));
  return tr;
}

function openCreateModal() {
  editingId = null;
  modalTitle.textContent = "Add Discipline Record";
  form.reset();
  form.status.value = "Open";
  clearFieldErrors();
  modalBackdrop.style.display = "flex";
}

function openEditModal(r) {
  editingId = r.id;
  modalTitle.textContent = "Edit Discipline Record";
  form.studentId.value = r.student ? r.student.id : "";
  form.incidentDate.value = r.incidentDate;
  form.fault.value = r.fault;
  form.description.value = r.description || "";
  form.punishment.value = r.punishment;
  form.points.value = r.points;
  form.status.value = r.status;
  clearFieldErrors();
  modalBackdrop.style.display = "flex";
}

function closeModal() {
  modalBackdrop.style.display = "none";
}

function openDeleteConfirm(id) {
  pendingDeleteId = id;
  deleteBackdrop.style.display = "flex";
}

function closeDeleteConfirm() {
  pendingDeleteId = null;
  deleteBackdrop.style.display = "none";
}

document.getElementById("add-record-btn").addEventListener("click", openCreateModal);
document.getElementById("cancel-btn").addEventListener("click", closeModal);
document.getElementById("cancel-delete-btn").addEventListener("click", closeDeleteConfirm);

document.getElementById("confirm-delete-btn").addEventListener("click", async () => {
  try {
    await api.del(`/discipline-records/${pendingDeleteId}`);
    closeDeleteConfirm();
    showBanner("Record deleted.", "success");
    loadRecords();
  } catch (err) {
    closeDeleteConfirm();
    showBanner(err.message, "error");
  }
});

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  clearFieldErrors();

  const payload = {
    studentId: form.studentId.value ? Number(form.studentId.value) : null,
    incidentDate: form.incidentDate.value,
    fault: form.fault.value.trim(),
    description: form.description.value.trim(),
    punishment: form.punishment.value.trim(),
    points: form.points.value !== "" ? Number(form.points.value) : null,
    status: form.status.value,
  };

  const submitBtn = form.querySelector("button[type=submit]");
  submitBtn.disabled = true;
  submitBtn.textContent = "Saving...";

  try {
    if (editingId) {
      await api.put(`/discipline-records/${editingId}`, payload);
      showBanner("Record updated.", "success");
    } else {
      await api.post("/discipline-records", payload);
      showBanner("Record created.", "success");
    }
    closeModal();
    loadRecords();
  } catch (err) {
    if (err.status === 400 && err.fields) {
      applyFieldErrors(err.fields);
    } else {
      showBanner(err.message, "error");
    }
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = "Save";
  }
});

scoreStudentSelect.addEventListener("change", async () => {
  const id = scoreStudentSelect.value;
  if (!id) {
    scoreValue.textContent = "—";
    return;
  }
  scoreValue.textContent = "...";
  try {
    const result = await api.get(`/discipline-records/student/${id}/score`);
    scoreValue.textContent = result.totalPoints;
  } catch (err) {
    scoreValue.textContent = "error";
  }
});

loadStudentsForDropdown().then(loadRecords);
