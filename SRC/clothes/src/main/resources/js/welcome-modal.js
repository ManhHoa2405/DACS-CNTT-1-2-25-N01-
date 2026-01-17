// If user unLoged in user
// document.addEventListener("DOMContentLoaded", function () {
//   const overlay = document.getElementById("welcomeOverlay");

//   const isLoggedIn = localStorage.getItem("isLoggedIn");

//   // CHƯA ĐĂNG NHẬP → HIỆN MODAL
//   if (!isLoggedIn) {
//     overlay.style.display = "flex";

//     // delay nhỏ để animation chạy
//     setTimeout(() => {
//       overlay.classList.add("active");
//     }, 50);
//   }
//   // ĐÃ ĐĂNG NHẬP → KHÔNG HIỆN
//   else {
//     overlay.style.display = "none";
//   }
// });

// /* ===== ĐÓNG MODAL ===== */
// function closeWelcome() {
//   const overlay = document.getElementById("welcomeOverlay");

//   overlay.classList.remove("active");
//   overlay.classList.add("hide");

//   // đợi animation xong rồi ẩn
//   setTimeout(() => {
//     overlay.style.display = "none";
//     overlay.classList.remove("hide");
//   }, 450);
// }

//
//
//
//
//
//
//
// ========================================================================
// Js for first-time visitors to the Modimal

document.addEventListener("DOMContentLoaded", function () {
  const overlay = document.getElementById("welcomeOverlay");
  const visited = localStorage.getItem("modimalVisited");

  if (!visited) {
    overlay.style.display = "flex";

    // delay nhỏ để animation chạy
    setTimeout(() => {
      overlay.classList.add("active");
    }, 50);
  } else {
    overlay.style.display = "none";
  }
});

function markVisited() {
  localStorage.setItem("modimalVisited", "true");
}

function closeWelcome() {
  const overlay = document.getElementById("welcomeOverlay");

  localStorage.setItem("modimalVisited", "true");

  overlay.classList.remove("active");
  overlay.classList.add("hide");

  // đợi animation xong rồi ẩn
  setTimeout(() => {
    overlay.style.display = "none";
    overlay.classList.remove("hide");
  }, 450);
}
