import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

const contentDiv = document.getElementById("content");


// ===============================
// Initial Load
// ===============================
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    // Add Doctor Modal Trigger
    const addBtn = document.getElementById("addDocBtn");
    if (addBtn) {
        addBtn.addEventListener("click", () => openModal("addDoctor"));
    }

    // Search + Filters
    document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
    document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
    document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);
});


// ===============================
// Load All Doctors
// ===============================
async function loadDoctorCards() {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
}


// ===============================
// Render Utility
// ===============================
function renderDoctorCards(doctors) {
    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }

    doctors.forEach(doc => {
        const card = createDoctorCard(doc);
        contentDiv.appendChild(card);
    });
}


// ===============================
// Search + Filter
// ===============================
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    const doctors = await filterDoctors(name, time, specialty);
    renderDoctorCards(doctors);
}


// ===============================
// Add Doctor Handler
// ===============================
window.adminAddDoctor = async function () {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Admin not authenticated!");
        return;
    }

    const name = document.getElementById("docName").value;
    const specialty = document.getElementById("docSpecialty").value;
    const email = document.getElementById("docEmail").value;
    const password = document.getElementById("docPassword").value;
    const mobile = document.getElementById("docMobile").value;

    const availabilityCheckboxes = document.querySelectorAll("input[name='availability']:checked");
    const availability = Array.from(availabilityCheckboxes).map(cb => cb.value);

    const doctor = {
        name,
        specialty,
        email,
        password,
        mobile,
        availability
    };

    const result = await saveDoctor(doctor, token);

    if (result.success) {
        alert(result.message);
        document.getElementById("addDoctorModal").style.display = "none";
        loadDoctorCards();
    } else {
        alert(result.message);
    }
};