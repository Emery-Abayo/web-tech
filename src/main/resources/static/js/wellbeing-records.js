const tableBody = document.getElementById("record-rows");
const stateBox = document.getElementById("state-box");
const banner = document.getElementById("banner");
const modalBackdrop = document.getElementById("modal-backdrop");
const modalTitle = document.getElementById("modal-title");
const form = document.getElementById("record-form");
const deleteBackdrop = document.getElementById("delete-backdrop");
const riskSelect = document.getElementById("riskLevel");
const followUpCheckbox = document.getElementById("followUpRequired");
const followUpNote = document.getElementById("follow-up-note");

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

async function loadStudentsForDropdown() {
  try {
    students = await api.get("/students");
    const optionsHtml = students.map((s) => `<option value="${s.id}">${escapeHtml(studentLabel(s))}</option>`).join("");
    form.studentId.innerHTML = `<option value="">Select student...</option>` + optionsHtml;
  } catch (err) {
    showBanner("Failed to load students for dropdown: " + err.message, "error");
  }
}

// Server forces followUpRequired = true whenever riskLevel is "High", regardless of
// what's sent. Mirror that here: lock and check the box, don't let the user fight it.
function syncFollowUpToRisk() {
  if (riskSelect.value === "High") {
    followUpCheckbox.checked = true;
    followUpCheckbox.disabled = true;
    followUpNote.style.display = "block";
  } else {
    followUpCheckbox.disabled = false;
    followUpNote.style.display = "none";
  }
}
riskSelect.addEventListener("change", syncFollowUpToRisk);

async function loadRecords() {
  stateBox.style.display = "block";
  stateBox.className = "loading-state";
  stateBox.textContent = "Loading wellbeing records...";
  tableBody.innerHTML = "";

  try {
    const records = await api.get("/wellbeing-records");
    if (records.length === 0) {
      stateBox.className = "empty-state";
      stateBox.textContent = "No wellbeing records yet. Click \"Add Record\" to create one.";
      return;
    }
    stateBox.style.display = "none";
    records.forEach((r) => tableBody.appendChild(renderRow(r)));
  } catch (err) {
    stateBox.className = "error-state";
    stateBox.textContent = "Failed to load wellbeing records: " + err.message;
  }
}

function renderRow(r) {
  const tr = document.createElement("tr");
  const studentName = r.student ? `${r.student.firstName} ${r.student.lastName}` : "(unknown)";
  const followUpBadge = r.followUpRequired
    ? `<span style="background:rgba(248,113,113,0.15); color:var(--danger); padding:0.15rem 0.55rem; border-radius:999px; font-size:0.78rem;">Yes</span>`
    : `<span style="background:rgba(148,163,184,0.15); color:var(--text-dim); padding:0.15rem 0.55rem; border-radius:999px; font-size:0.78rem;">No</span>`;
  tr.innerHTML = `
    <td>${r.id}</td>
    <td>${escapeHtml(studentName)}</td>
    <td>${escapeHtml(r.checkDate)}</td>
    <td>${escapeHtml(r.concernType)}</td>
    <td>${escapeHtml(r.riskLevel)}</td>
    <td>${followUpBadge}</td>
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
  modalTitle.textContent = "Add Wellbeing Record";
  form.reset();
  form.status.value = "Open";
  syncFollowUpToRisk();
  clearFieldErrors();
  modalBackdrop.style.display = "flex";
}

function openEditModal(r) {
  editingId = r.id;
  modalTitle.textContent = "Edit Wellbeing Record";
  form.studentId.value = r.student ? r.student.id : "";
  form.checkDate.value = r.checkDate;
  form.concernType.value = r.concernType;
  form.notes.value = r.notes || "";
  form.riskLevel.value = r.riskLevel;
  followUpCheckbox.checked = !!r.followUpRequired;
  form.status.value = r.status;
  syncFollowUpToRisk();
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
    await api.del(`/wellbeing-records/${pendingDeleteId}`);
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
    checkDate: form.checkDate.value,
    concernType: form.concernType.value.trim(),
    notes: form.notes.value.trim(),
    riskLevel: form.riskLevel.value,
    followUpRequired: followUpCheckbox.checked,
    status: form.status.value,
  };

  const submitBtn = form.querySelector("button[type=submit]");
  submitBtn.disabled = true;
  submitBtn.textContent = "Saving...";

  try {
    if (editingId) {
      await api.put(`/wellbeing-records/${editingId}`, payload);
      showBanner("Record updated.", "success");
    } else {
      await api.post("/wellbeing-records", payload);
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

loadStudentsForDropdown().then(loadRecords);
