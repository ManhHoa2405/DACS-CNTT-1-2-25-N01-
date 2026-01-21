const addressListView = document.getElementById("addressListView");
const addAddressForm = document.getElementById("addAddressForm");
const modalTitle = document.getElementById("modalTitle");
const backBtn = document.getElementById("backBtn");
const closeBtnModal = document.getElementById("closeBtn");

// Hàm chuyển sang màn hình thêm mới
function showAddAddressForm() {
  addressListView.style.display = "none";
  addAddressForm.style.display = "block";
  modalTitle.innerText = "Địa Chỉ Mới";
  backBtn.style.display = "block";
  closeBtnModal.style.display = "none";

  // Gọi API lấy tỉnh thành nếu chưa có dữ liệu
  if (document.getElementById("province").options.length <= 1) {
    loadProvinces();
  }
}

// Hàm quay lại danh sách
function backToListView() {
  addressListView.style.display = "block";
  addAddressForm.style.display = "none";
  modalTitle.innerText = "Địa Chỉ Của Tôi";
  backBtn.style.display = "none";
  closeBtnModal.style.display = "block";
}

// Logic API Provinces (Giữ nguyên như phần trước đã hướng dẫn)
const host = "https://provinces.open-api.vn/api/";
function loadProvinces() {
  fetch(host + "?depth=1")
    .then((res) => res.json())
    .then((data) => renderData(data, "province"));
}

function renderData(array, selectId) {
  let options = `<option value="">Chọn</option>`;
  array.forEach((item) => {
    options += `<option value="${item.code}">${item.name}</option>`;
  });
  document.getElementById(selectId).innerHTML = options;
}

// Xử lý thay đổi Tỉnh -> Hiện Huyện
document.getElementById("province").addEventListener("change", function () {
  if (this.value) {
    fetch(`${host}p/${this.value}?depth=2`)
      .then((res) => res.json())
      .then((data) => {
        renderData(data.districts, "district");
        document.getElementById("district").disabled = false;
      });
  }
});

// Xử lý thay đổi Huyện -> Hiện Xã
document.getElementById("district").addEventListener("change", function () {
  if (this.value) {
    fetch(`${host}d/${this.value}?depth=2`)
      .then((res) => res.json())
      .then((data) => {
        renderData(data.wards, "ward");
        document.getElementById("ward").disabled = false;
      });
  }
});
