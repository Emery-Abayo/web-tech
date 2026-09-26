const tableBody = document.getElementById("student-rows");
const stateBox = document.getElementById("state-box");
const banner = document.getElementById("banner");
const modalBackdrop = document.getElementById("modal-backdrop");
const modalTitle = document.getElementById("modal-title");
const form = document.getElementById("student-form");
const deleteBackdrop = document.getElementById("delete-backdrop");

let editingId = null;
let pendingDeleteId = null;

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

async function loadStudents() {
  stateBox.style.display = "block";
  stateBox.className = "loading-state";
  stateBox.textContent = "Loading students...";
  tableBody.innerHTML = "";

  try {
    const students = await api.get("/students");
    if (students.length === 0) {
      stateBox.className = "empty-state";
      stateBox.textContent = "No students yet. Click \"Add Student\" to create one.";
      return;
    }
    stateBox.style.display = "none";
    students.forEach((s) => tableBody.appendChild(renderRow(s)));
  } catch (err) {
    stateBox.className = "error-state";
    stateBox.textContent = "Failed to load students: " + err.message;
  }
}

function renderRow(s) {
  const tr = document.createElement("tr");
  tr.innerHTML = `
    <td>${s.id}</td>
    <td>${escapeHtml(s.studentNumber)}</td>
    <td>${escapeHtml(s.firstName)} ${escapeHtml(s.lastName)}</td>
    <td>${escapeHtml(s.className)}</td>
    <td>${escapeHtml(s.gender || "")}</td>
    <td>${escapeHtml(s.email || "")}</td>
    <td class="actions-cell">
      <button class="secondary small" data-edit="${s.id}">Edit</button>
      <button class="danger small" data-delete="${s.id}">Delete</button>
    </td>
  `;
  tr.querySelector("[data-edit]").addEventListener("click", () => openEditModal(s));
  tr.querySelector("[data-delete]").addEventListener("click", () => openDeleteConfirm(s.id));
  return tr;
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

function openCreateModal() {
  editingId = null;
  modalTitle.textContent = "Add Student";
  form.reset();
  clearFieldErrors();
  modalBackdrop.style.display = "flex";
}

function openEditModal(s) {
  editingId = s.id;
  modalTitle.textContent = "Edit Student";
  form.studentNumber.value = s.studentNumber;
  form.firstName.value = s.firstName;
  form.lastName.value = s.lastName;
  form.gender.value = s.gender || "";
  form.className.value = s.className;
  form.email.value = s.email || "";
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

document.getElementById("add-student-btn").addEventListener("click", openCreateModal);
document.getElementById("cancel-btn").addEventListener("click", closeModal);
document.getElementById("cancel-delete-btn").addEventListener("click", closeDeleteConfirm);

document.getElementById("confirm-delete-btn").addEventListener("click", async () => {
  try {
    await api.del(`/students/${pendingDeleteId}`);
    closeDeleteConfirm();
    showBanner("Student deleted.", "success");
    loadStudents();
  } catch (err) {
    closeDeleteConfirm();
    // Backend returns 409 when the student has discipline/wellbeing history.
    showBanner(err.message, "error");
  }
});

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  clearFieldErrors();

  const payload = {
    studentNumber: form.studentNumber.value.trim(),
    firstName: form.firstName.value.trim(),
    lastName: form.lastName.value.trim(),
    gender: form.gender.value.trim(),
    className: form.className.value.trim(),
    email: form.email.value.trim(),
  };

  const submitBtn = form.querySelector("button[type=submit]");
  submitBtn.disabled = true;
  submitBtn.textContent = "Saving...";

  try {
    if (editingId) {
      await api.put(`/students/${editingId}`, payload);
      showBanner("Student updated.", "success");
    } else {
      await api.post("/students", payload);
      showBanner("Student created.", "success");
    }
    closeModal();
    loadStudents();
  } catch (err) {
    if (err.status === 400 && err.fields) {
      applyFieldErrors(err.fields);
    } else {
      // 409 (duplicate student number) or unexpected errors surface here.
      showBanner(err.message, "error");
    }
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = "Save";
  }
});

loadStudents();
