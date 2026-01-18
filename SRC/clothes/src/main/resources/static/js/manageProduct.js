
    document.addEventListener("click", function(e) {
        
        // --- 1. XEM CHI TIẾT ---
        if (e.target.closest(".btn-detail")) {
            const btn = e.target.closest(".btn-detail");
            const tr = btn.closest("tr");
            const skuRow = tr.nextElementSibling;
            if (skuRow && skuRow.classList.contains("sku-row")) {
                skuRow.classList.toggle("show");
                btn.textContent = skuRow.classList.contains("show") ? "▲ Thu gọn" : "▼ Chi tiết";
            }
        }

        // --- 2. KHI BẤM SỬA (✏️) ---
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

        // --- 3. KHI BẤM HỦY (✖) ---
        if (e.target.closest(".btn-cancel")) {
            const row = e.target.closest("tr");
            const span = row.querySelector(".stock-display");
            const input = row.querySelector(".stock-input");
            
            // Hiện lại Span -> Ẩn Input
            span.style.display = "inline-block";
            input.style.display = "none";
            
            toggleButtons(row, false);
        }

        // --- 4. KHI BẤM LƯU (✔) ---
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
                    alert("Cập nhật thành công!");
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
                alert("Vui lòng nhập Size và Số lượng!"); 
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
                        <td style="vertical-align: middle;"><span>-</span></td>
                        <td style="vertical-align: middle;">
                             <button class="btn-edit" style="cursor: pointer; border: none; background: none; font-size: 1.2em;">✏️</button>
                             <button class="btn-save" style="display: none; cursor: pointer; border: none; background: none; color: green; font-size: 1.2em;">✔</button>
                             <button class="btn-cancel" style="display: none; cursor: pointer; border: none; background: none; color: red; font-size: 1.2em;">✖</button>
                             <button class="btn-delete" style="cursor: pointer; border: none; background: none; color: red; font-size: 1.2em; margin-left: 10px;">🗑️</button>
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
