
    document.addEventListener("click", function(e) {
        
        // --- 1. XEM CHI TIẾT ---
        if (e.target.closest(".btn-detail")) {
            const btn = e.target.closest(".btn-detail");
            const tr = btn.closest("tr");
            const skuRow = tr.nextElementSibling;
            if (skuRow && skuRow.classList.contains("sku-row")) {
                skuRow.classList.toggle("show");
                // btn.textContent = skuRow.classList.contains("show") ? "▲ Thu gọn" : "▼ Chi tiết";
                 btn.innerHTML = skuRow.classList.contains("show")
                ? '<i class="fa-solid fa-chevron-up"></i> Thu gọn'
                : '<i class="fa-solid fa-chevron-down"></i> Chi tiết';
            }
        }

        // --- 2. KHI BẤM SỬA () ---
        if (e.target.closest(".btn-edit")) {
            const row = e.target.closest("tr");
            const span = row.querySelector(".stock-display");
            const input = row.querySelector(".stock-input");
            
            // Ẩn Span -> Hiện Input
            span.style.display = "none";
            input.style.display = "inline-block";
            input.value = span.textContent; // Đồng bộ dữ liệu
            input.focus();

            toggleButtons(row, true);
        }

        // --- 3. KHI BẤM HỦY () ---
        if (e.target.closest(".btn-cancel")) {
            const row = e.target.closest("tr");
            const span = row.querySelector(".stock-display");
            const input = row.querySelector(".stock-input");
            
            // Hiện lại Span -> Ẩn Input
            span.style.display = "inline-block";
            input.style.display = "none";
            
            toggleButtons(row, false);
        }

        // --- 4. KHI BẤM LƯU () ---
        if (e.target.closest(".btn-save")) {
            const row = e.target.closest("tr");
            const span = row.querySelector(".stock-display");
            const input = row.querySelector(".stock-input");
            const id = row.getAttribute("data-id");
            const newVal = parseInt(input.value);

            if (isNaN(newVal) || newVal < 0) { alert("Số lượng sai!"); return; }

            fetch('/admin/api/update-sku', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: id, stock: newVal })
            }).then(res => {
                if (res.ok) {
                    // Cập nhật số mới lên màn hình
                    span.textContent = newVal;
                    
                    // Ẩn Input -> Hiện Span
                    span.style.display = "inline-block";
                    input.style.display = "none";
                    
                    toggleButtons(row, false);
                    updateParentTotal(row);
                    alert("Cập nhật thành công");
                } else {
                    alert("Lỗi server!");
                }
            });
        }

        // --- 5. XÓA ---
        if (e.target.closest(".btn-delete")) {
            if(!confirm("Xóa nhé?")) return;
            const row = e.target.closest("tr");
            const id = row.getAttribute("data-id");

            fetch('/admin/api/delete-sku', {
                method: 'POST', headers: {'Content-Type':'application/json'},
                body: JSON.stringify({id: id})
            }).then(res => {
                if(res.ok) {
                    const table = row.closest("table");
                    row.remove();
                    updateParentTotal(null, table);
                }
            });
        }

        // 6. THÊM MỚI (LOGIC CẬP NHẬT CHO FORM RIÊNG)
        if (e.target.closest(".btn-add-confirm")) {
            // Tìm cái hộp chứa form thêm mới (class="add-form")
            const addForm = e.target.closest(".add-form");
            
            // Từ hộp form, tìm ra bảng và dòng cha để lấy ID sản phẩm
            const skuBox = addForm.closest(".sku-box");
            const table = skuBox.querySelector("table"); // Lấy bảng để lát chèn dòng mới vào
            const skuRowContainer = skuBox.closest("tr.sku-row");
            const productId = skuRowContainer.getAttribute("data-parent-id");
            
            // Lấy giá trị từ 2 ô input nằm trong addForm
            const sizeInp = addForm.querySelector(".new-size");
            const stockInp = addForm.querySelector(".new-stock");

            if (!sizeInp.value || !stockInp.value) { 
                alert("Vui lòng nhập Size và Số lượng"); 
                sizeInp.focus();
                return; 
            }

            fetch('/admin/api/add-sku', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId: productId, size: sizeInp.value, stock: stockInp.value })
            })
            .then(res => res.json())
            .then(data => {
                if (data.newId) {
                    const newRow = document.createElement("tr");
                    newRow.setAttribute("data-id", data.newId);
                    
                    // Tạo dòng HTML mới chèn vào bảng
                    newRow.innerHTML = `
                        <td style="vertical-align: middle;"><span style="font-weight: bold;">${sizeInp.value}</span></td>
                        <td style="vertical-align: middle;">
                            <span class="stock-display" style="font-weight: bold; font-size: 1.1em; color: #333;">${stockInp.value}</span>
                            <input type="number" class="stock-input" value="${stockInp.value}" min="0" 
                                   style="display: none; width: 80px; text-align: center; border: 1px solid #007bff;">
                        </td>
                        
                        <td style="vertical-align: middle;">
                             <button class="btn-edit" style="cursor: pointer; border: none; background: none; font-size: 1.2em; color:black;"><i class="fa-solid fa-pen"></i></button>
                             <button class="btn-save" style="display: none; cursor: pointer; border: none; background: none; color: green; font-size: 1.2em;"><i class="fa-solid fa-check"></i></button>
                             <button class="btn-cancel" style="display: none; cursor: pointer; border: none; background: none; color: red; font-size: 1.2em;"><i class="fa-solid fa-xmark"></i></button>
                             <button class="btn-delete" style="cursor: pointer; border: none; background: none; color: red; font-size: 1.2em; margin-left: 10px;"><i class="fa-solid fa-trash"></i></button>
                        </td>
                    `;
                    
                    table.querySelector("tbody").appendChild(newRow);
                    
                    // Reset ô nhập
                    sizeInp.value = ""; 
                    stockInp.value = "";
                    
                    updateParentTotal(newRow);
                    alert("Thêm thành công!");
                }
            })
            .catch(err => {
                console.error(err);
                alert("Lỗi server hoặc chưa có API Add!");
            });
        }
    });

    // --- HÀM PHỤ TRỢ ---
    function toggleButtons(row, isEditing) {
        row.querySelector(".btn-edit").style.display = isEditing ? "none" : "inline-block";
        row.querySelector(".btn-delete").style.display = isEditing ? "none" : "inline-block";
        row.querySelector(".btn-save").style.display = isEditing ? "inline-block" : "none";
        row.querySelector(".btn-cancel").style.display = isEditing ? "inline-block" : "none";
    }

    function updateParentTotal(row, tableRef = null) {
        const table = row ? row.closest("table") : tableRef;
        if (!table) return;
        let total = 0;
        // Tính tổng dựa trên số đang hiện ở thẻ SPAN
        table.querySelectorAll(".stock-display").forEach(span => total += parseInt(span.textContent || 0));
        
        const skuRowContainer = table.closest("tr.sku-row");
        if(skuRowContainer) {
            const parentId = skuRowContainer.getAttribute("data-parent-id");
            const totalCell = document.getElementById("total-stock-" + parentId);
            if(totalCell) {
                totalCell.textContent = total;
                totalCell.style.color = "green";
                setTimeout(() => totalCell.style.color = "", 1000);
            }
        }
    }

    // --- LOGIC QUẢN LÝ SẢN PHẨM CHA (PRODUCT) ---

// 1. BẤM NÚT SỬA SẢN PHẨM
document.addEventListener("click", function(e) {
    if (e.target.closest(".btn-edit-product")) {
        const row = e.target.closest("tr.product-row");
        
        // Ẩn view, Hiện edit
        row.querySelectorAll(".display-mode, .action-group-view").forEach(el => el.style.display = "none");
        row.querySelectorAll(".edit-mode").forEach(el => el.style.display = "flex"); // Flex cho ô tên/ảnh
        row.querySelector(".edit-price").style.display = "block";
        row.querySelector(".edit-status").style.display = "block";
        row.querySelector(".action-group-edit").style.display = "block";
    }

    // 2. BẤM HỦY
    if (e.target.closest(".btn-cancel-product")) {
        const row = e.target.closest("tr.product-row");
        // Reset lại giao diện
        row.querySelectorAll(".display-mode").forEach(el => el.style.display = (el.tagName === "DIV" ? "flex" : "inline"));
        row.querySelector(".action-group-view").style.display = "block";
        
        row.querySelectorAll(".edit-mode, .action-group-edit").forEach(el => el.style.display = "none");
    }

    // 3. BẤM LƯU (CẬP NHẬT SẢN PHẨM KÈM ẢNH)
if (e.target.closest(".btn-save-product")) {
    const row = e.target.closest("tr.product-row");
    const id = row.getAttribute("data-id");
    
    // Lấy dữ liệu text
    const name = row.querySelector(".edit-name").value;
    const price = row.querySelector(".edit-price").value;
    const status = row.querySelector(".edit-status").value;

    //  QUAN TRỌNG: Lấy danh sách các file đã chọn
    const imageFiles = row.querySelector(".edit-img-file").files;

    if (!name || !price) { alert("Tên và giá không được để trống!"); return; }

    //  Dùng FormData để đóng gói dữ liệu + file
    const formData = new FormData();
    formData.append('id', id);
    formData.append('name', name);
    formData.append('price', price);
    formData.append('status', status);

    // Duyệt và thêm từng file ảnh vào FormData
    for (let i = 0; i < imageFiles.length; i++) {
        formData.append('imageFiles', imageFiles[i]);
    }

    // Gửi AJAX request (Lưu ý: Không set 'Content-Type' thủ công)
    fetch('/admin/api/update-product-with-files', { //  Đổi đường dẫn API mới
        method: 'POST',
        body: formData
    })
    .then(res => {
        if (res.ok) {
            alert("Đã cập nhật sản phẩm và ảnh thành công");
            location.reload(); // Load lại trang để thấy ảnh mới
        } else {
            res.text().then(text => alert("Lỗi server: " + text));
        }
    })
    .catch(err => alert("Lỗi kết nối: " + err));
}

    // 4. BẤM XÓA SẢN PHẨM
    if (e.target.closest(".btn-delete-product")) {
        if (!confirm("CẢNH BÁO: Xóa sản phẩm này sẽ xóa toàn bộ SKU và Lịch sử đơn hàng liên quan!\nBạn có chắc chắn không?")) return;
        
        const row = e.target.closest("tr.product-row");
        const id = row.getAttribute("data-id");

        fetch('/admin/api/delete-product', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id: id })
        })
        .then(res => {
            if (res.ok) {
                row.remove(); // Xóa dòng cha
                // Xóa luôn dòng SKU con (nằm ngay bên dưới)
                const skuRow = document.querySelector(`.sku-row[data-parent-id="${id}"]`);
                if (skuRow) skuRow.remove();
                
                alert("Đã xóa sản phẩm!");
            } else {
                alert("Không thể xóa (Có thể do ràng buộc dữ liệu)!");
            }
        });
    }
});