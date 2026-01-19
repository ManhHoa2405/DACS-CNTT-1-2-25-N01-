document.addEventListener("DOMContentLoaded", function () {
  const minusBtn = document.querySelector(".qty-btn.minus");
  const plusBtn = document.querySelector(".qty-btn.plus");
  const qtyInput = document.querySelector(".qty-input");

  if (!minusBtn || !plusBtn || !qtyInput) return;

  plusBtn.addEventListener("click", function () {
    let current = parseInt(qtyInput.value) || 1;
    qtyInput.value = current + 1;
  });

  minusBtn.addEventListener("click", function () {
    let current = parseInt(qtyInput.value) || 1;
    if (current > 1) {
      qtyInput.value = current - 1;
    }
  });

  // Không cho nhập số <= 0
  qtyInput.addEventListener("input", function () {
    if (qtyInput.value < 1 || qtyInput.value === "") {
      qtyInput.value = 1;
    }
  });
});
