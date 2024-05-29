    document.getElementById('exportJson').addEventListener('click', function() {
    exportJsonData();
});

    document.getElementById('exportXlsx').addEventListener('click', function() {
    exportExcelData();
});

    function exportJsonData() {
    var table = document.getElementById('data');
    var rows = table.querySelectorAll('tr');
    var jsonData = {};

    rows.forEach(function(row) {
    var cells = row.querySelectorAll('td');
    var currentObject = jsonData;

    for (var i = 0; i < cells.length - 1; i++) {
    var key = cells[i].querySelector('span').innerText;
    var input = cells[i + 1].querySelector('input[type="text"]');

    var value = input ? input.value : '';

    if (!(key in currentObject)) {
    currentObject[key] = {};
}

    if (i === cells.length - 2) {
    if (/\w+\[\d+\]/.test(key)) {
    var arrKey = key.substring(0, key.indexOf('['));
    if (!currentObject[arrKey]) {
    currentObject[arrKey] = [];
}
    currentObject[arrKey].push(value);
} else {
    currentObject[key] = value;
}
} else {
    if (!currentObject[key]) {
    currentObject[key] = {};
}
    currentObject = currentObject[key];
}
}
});

    removeEmptyKeys(jsonData);

    var jsonContent = JSON.stringify(jsonData, null, 2);

    var dataUri = 'data:application/json;charset=utf-8,' + encodeURIComponent(jsonContent);

    var downloadLink = document.createElement('a');
    downloadLink.href = dataUri;
    downloadLink.download = 'data.json';

    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
}

    function exportExcelData() {
    var table = document.getElementById('data');
    var ws = XLSX.utils.table_to_sheet(table);

    var textInputs = table.querySelectorAll('input[type="text"]');
    textInputs.forEach(function(input) {
    var cell = input.parentNode;
    var columnIndex = Array.from(cell.parentNode.children).indexOf(cell);
    var rowIndex = Array.from(cell.parentNode.parentNode.children).indexOf(cell.parentNode);
    var cellAddress = XLSX.utils.encode_cell({ r: rowIndex, c: columnIndex });
    ws[cellAddress] = { t: 's', v: input.value };
});

    var wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    XLSX.writeFile(wb, 'data.xlsx');
}

    function removeEmptyKeys(obj) {
    for (var key in obj) {
    if (typeof obj[key] === 'object') {
    removeEmptyKeys(obj[key]);
    if (Object.keys(obj[key]).length === 0) {
    delete obj[key];
}
} else {
    if (obj[key] === '') {
    delete obj[key];
}
}
}
}

    document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById('searchInput');
    const rows = document.querySelectorAll('#data tbody tr');

    searchInput.addEventListener('input', function () {
    const searchText = searchInput.value.toLowerCase();
    rows.forEach(row => {
    let found = false;
    row.querySelectorAll('td').forEach(cell => {
    if (cell.textContent.toLowerCase().includes(searchText)) {
    found = true;
}
});
    row.style.display = found ? '' : 'none';
});
});
});