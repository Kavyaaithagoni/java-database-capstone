import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0];
let token = localStorage.getItem("token");
let patientName = null;


// ===============================
// Initial Load
// ===============================
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("datePicker").value = selectedDate;
    loadAppointments();

    // Search Bar
    document.getElementById("searchBar")?.addEventListener("input", (e) => {
        patientName = e.target.value || "null";
        loadAppointments();
    });

    // Today Button
    document.getElementById("todayButton")?.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split("T")[0];
        document.getElementById("datePicker").value = selectedDate;
        loadAppointments();
    });

    // Date Picker
    document.getElementById("datePicker")?.addEventListener("change", (e) => {
        selectedDate = e.target.value;
        loadAppointments();
    });
});


// ===============================
// Load Appointments
// ===============================
async function loadAppointments() {
    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML =
                `<tr><td colspan="5">No Appointments found for selected date</td></tr>`;
            return;
        }

        appointments.forEach(app => {
            const row = createPatientRow(app);
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading appointments:", error);
        tableBody.innerHTML =
            `<tr><td colspan="5">Failed to load appointments</td></tr>`;
    }
}