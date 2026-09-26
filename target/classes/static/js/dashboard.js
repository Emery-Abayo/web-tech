const statsGrid = document.getElementById("stats-grid");
const stateBox = document.getElementById("state-box");

function statCard(label, value, color) {
  return `
    <div style="background:var(--surface); border:1px solid var(--border); border-radius:8px; padding:1.1rem 1.3rem;">
      <div style="color:var(--text-dim); font-size:0.8rem; margin-bottom:0.4rem;">${label}</div>
      <div style="color:${color || "var(--text)"}; font-size:1.8rem; font-weight:700;">${value}</div>
    </div>
  `;
}

async function loadDashboard() {
  stateBox.style.display = "block";
  stateBox.className = "loading-state";
  stateBox.textContent = "Loading overview...";

  try {
    const [students, discipline, wellbeing] = await Promise.all([
      api.get("/students"),
      api.get("/discipline-records"),
      api.get("/wellbeing-records"),
    ]);

    const openDiscipline = discipline.filter((r) => r.status === "Open").length;
    const highRisk = wellbeing.filter((r) => (r.riskLevel || "").toLowerCase() === "high").length;
    const pendingFollowUp = wellbeing.filter((r) => r.followUpRequired && r.status === "Open").length;

    stateBox.style.display = "none";
    statsGrid.innerHTML =
      statCard("Total Students", students.length) +
      statCard("Discipline Records", discipline.length) +
      statCard("Open Discipline Cases", openDiscipline, openDiscipline > 0 ? "var(--warn)" : "var(--text)") +
      statCard("Wellbeing Records", wellbeing.length) +
      statCard("High Risk Check-ins", highRisk, highRisk > 0 ? "var(--danger)" : "var(--text)") +
      statCard("Pending Follow-ups", pendingFollowUp, pendingFollowUp > 0 ? "var(--danger)" : "var(--success)");
  } catch (err) {
    stateBox.className = "error-state";
    stateBox.textContent = "Failed to load overview: " + err.message;
  }
}

loadDashboard();
