<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tạo Tác Vụ</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <c:if test="${empty sessionScope.user}">
        <c:redirect url="login.jsp"/>
    </c:if>

    <div class="container">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px;">
            <h3>Chào, ${sessionScope.user.username}!</h3>
            <div>
                <a href="history" class="button">Xem lịch sử</a>
                <a href="logout" class="button" style="background-color: #dc3545;">Đăng xuất</a>
            </div>
        </div>
        
        <h2 style="margin-top: 30px;">Tạo Tác Vụ Xử Lý Ảnh Mới</h2>
        
        <form action="upload" method="post" enctype="multipart/form-data" id="uploadForm">
            
            <label for="job_type">1. Chọn loại xử lý:</label>
            <select name="job_type" id="job_type_select">
                <option value="WATERMARK">Đóng dấu ảnh (Watermark)</option>
                <option value="RESIZE">Thay đổi kích thước (Resize)</option>
                <option value="GRAYSCALE">Chuyển ảnh trắng đen (Grayscale)</option>
            </select>
            
            <div id="params_watermark" class="job-params-group">
                <label for="param_watermark">Nhập chữ muốn đóng dấu:</label>
                <input type="text" name="param_watermark" value="Copyright by ${sessionScope.user.username}">
            </div>
            <div id="params_resize" class="job-params-group">
                <label for="param_resize">Nhập chiều rộng mong muốn (px):</label>
                <input type="number" name="param_resize" value="800">
            </div>
            
            <label for="upload_mode">2. Chọn chế độ upload:</label>
            <select name="upload_mode" id="upload_mode_select">
                <option value="single_image" selected>Upload 1 file ảnh</option>
                <option value="multi_image">Upload nhiều file ảnh</option>
                <option value="single_zip">Upload 1 file .zip</option>
            </select>
            
            <label for="imageFile" id="file_input_label">3. Chọn file (Ảnh đơn):</label>
            <input type="file" name="imageFile" id="file_input" required>
            
            <input type="hidden" name="job_params" id="job_params_hidden">
            
            <input type="submit" value="Upload và Bắt đầu xử lý">
        </form>
    </div>

    <script>
        const jobSelect = document.getElementById('job_type_select');
        const paramsWatermark = document.getElementById('params_watermark');
        const paramsResize = document.getElementById('params_resize');
        const form = document.getElementById('uploadForm');
        const hiddenParamsInput = document.getElementById('job_params_hidden');
        
        const modeSelect = document.getElementById('upload_mode_select');
        const fileInput = document.getElementById('file_input');
        const fileLabel = document.getElementById('file_input_label');

        function toggleJobParams() {
            paramsWatermark.style.display = 'none';
            paramsResize.style.display = 'none';
            const selectedJob = jobSelect.value;
            if (selectedJob === 'WATERMARK') {
                paramsWatermark.style.display = 'flex';
            } else if (selectedJob === 'RESIZE') {
                paramsResize.style.display = 'flex';
            }
        }
        
        function toggleUploadMode() {
            const selectedMode = modeSelect.value;
            if (selectedMode === 'single_image') {
                fileLabel.innerText = '3. Chọn file (Ảnh đơn):';
                fileInput.multiple = false;
                fileInput.accept = 'image/png, image/jpeg';
            } else if (selectedMode === 'multi_image') {
                fileLabel.innerText = '3. Chọn files (Nhiều ảnh):';
                fileInput.multiple = true;
                fileInput.accept = 'image/png, image/jpeg';
            } else if (selectedMode === 'single_zip') {
                fileLabel.innerText = '3. Chọn file (1 file ZIP):';
                fileInput.multiple = false;
                fileInput.accept = '.zip, application/zip';
            }
        }

        form.addEventListener('submit', function(e) {
            const selectedJob = jobSelect.value;
            if (selectedJob === 'WATERMARK') {
                hiddenParamsInput.value = document.querySelector('input[name="param_watermark"]').value;
            } else if (selectedJob === 'RESIZE') {
                hiddenParamsInput.value = document.querySelector('input[name="param_resize"]').value;
            } else {
                hiddenParamsInput.value = 'N/A';
            }
        });
        
        toggleJobParams();
        toggleUploadMode();
        
        jobSelect.addEventListener('change', toggleJobParams);
        modeSelect.addEventListener('change', toggleUploadMode);
    </script>
</body>
</html>