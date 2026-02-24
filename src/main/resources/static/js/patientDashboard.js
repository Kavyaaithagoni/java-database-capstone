import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";

const contentDiv = document.getElementById("content");


// ===============================
// Initial Load
// ===============================
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    document.getElementById("patientSignup")?.addEventListener("click", () => openModal("patientSignup"));
    document.getElementById("patientLogin")?.addEventListener("click", () => openModal("patientLogin"));

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
        contentDiv.appendChild(createDoctorCard(doc));
    });
}


// ===============================
// Filter Doctors
// ===============================
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("filterTime")?.value || "";
    const specialty = document.getElementById("filterSpecialty")?.value || "";

    const doctors = await filterDoctors(name, time, specialty);
    renderDoctorCards(doctors);
}


// ===============================
// Patient Signup
// ===============================
window.signupPatient = async function () {
    const data = {
        name: document.getElementById("signupName").value,
        email: document.getElementById("signupEmail").value,
        password: document.getElementById("signupPassword").value,
        phone: document.getElementById("signupPhone").value,
        address: document.getElementById("signupAddress").value
    };

    const result = await patientSignup(data);

    if (result.success) {
        alert(result.message);
        location.reload();
    } else {
        alert(result.message);
    }
};


// ===============================
// Patient Login
// ===============================
window.loginPatient = async function () {
    const data = {
        email: document.getElementById("loginEmail").value,
        password: document.getElementById("loginPassword").value
    };

    try {
        const response = await patientLogin(data);

        if (response.ok) {
            const result = await response.json();
            localStorage.setItem("token", result.token);
            window.location.href = "loggedPatientDashboard.html";
        } else {
            alert("Invalid credentials!");
        }

    } catch (error) {
        console.error("Login error:", error);
        alert("Login failed");
    }
};