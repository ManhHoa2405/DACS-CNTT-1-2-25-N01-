// apiAddress.js
const addressListView = document.getElementById("view-list"); // Đổi ID cho khớp HTML
const addAddressForm = document.getElementById("view-form"); // Đổi ID cho khớp HTML
const modalTitle = document.getElementById("modalTitle");

// Hàm chuyển sang màn hình thêm mới
function showAddAddressForm() {
  addressListView.style.display = "none";
  addAddressForm.style.display = "block";
  modalTitle.innerText = "Thêm Địa Chỉ Mới";

  // Gọi API lấy tỉnh thành nếu chưa có dữ liệu
  if (document.getElementById("province").options.length <= 1) {
    loadProvinces();
  }
}

// Hàm quay lại danh sách
function backToListView() {
  addressListView.style.display = "block";
  addAddressForm.style.display = "none";
  modalTitle.innerText = "Danh Sách Địa Chỉ";
}

// Logic API Provinces
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
  const districtSelect = document.getElementById("district");
  const wardSelect = document.getElementById("ward");
  districtSelect.innerHTML = '<option value="">Quận/Huyện</option>';
  wardSelect.innerHTML = '<option value="">Phường/Xã</option>';
  districtSelect.disabled = true;
  wardSelect.disabled = true;

  if (this.value) {
    fetch(`${host}p/${this.value}?depth=2`)
      .then((res) => res.json())
      .then((data) => {
        renderData(data.districts, "district");
        districtSelect.disabled = false;
      });
  }
});

// Xử lý thay đổi Huyện -> Hiện Xã
document.getElementById("district").addEventListener("change", function () {
  const wardSelect = document.getElementById("ward");
  wardSelect.innerHTML = '<option value="">Phường/Xã</option>';
  wardSelect.disabled = true;

  if (this.value) {
    fetch(`${host}d/${this.value}?depth=2`)
      .then((res) => res.json())
      .then((data) => {
        renderData(data.wards, "ward");
        wardSelect.disabled = false;
      });
  }
});
