(async () => {
    let currentUser = await (await fetch("/api/authentication")).json();
    if (currentUser.authorities.includes("ADMIN")) {
        fillTable(document.getElementById("all-users-table-body"), await (await fetch("/api/")).json())
        document.getElementById("Create").addEventListener("submit", handleCreateForm)
        document.title = "Панель администратора"
        switchActivation(["", "v-pills-admin"])
    } else if (currentUser.authorities.includes("USER")) {
        document.title = "Страница пользователя"
        switchActivation(["", "v-pills-user"])
    } else {
        alert("Доступ запрещён!")
        return
    }
    fillHTML(["username", "authorities"], "Navbar", currentUser, " ")
    fillHTML(["id", "username", "surname", "name", "patronymic", "birthDate", "authorities"], "CurrentUser", currentUser, " ")
})()

function createUser(userMap) {
    return fetch("/api/", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-Token": userMap.get("_csrf")
        },
        body: JSON.stringify(Object.fromEntries(userMap))
    })
}

function editUser(userMap) {
    return fetch("/api/", {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-Token": userMap.get("_csrf")
        },
        body: JSON.stringify(Object.fromEntries(userMap))
    })
}

async function deleteUserById(userMap) {
    return fetch("/api/" + userMap.get("id"), {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-Token": userMap.get("_csrf")
        }
    })
}

async function handleCreateForm(event) {
    event.preventDefault()
    let response = await createUser(mapFromTarget(event.target))
    if (response.ok) {
        fillHTML(["username", "password", "surname", "name", "patronymic", "birthDate"], "NewUserErrors", "", "<br>")
        appendToTable(document.getElementById("all-users-table-body"), await (await fetch(response.headers.get("location"))).json())
        switchActivation(["nav-new-user", "nav-users-table"])
        event.target.reset()
    } else {
        let errors = await response.json()
        fillHTML(["username", "password", "surname", "name", "patronymic", "birthDate"], "NewUserErrors", errors, "<br>")
    }
}

async function handleEditModal(id) {
    fillForm(["id", "username", "surname", "name", "patronymic", "birthDate", "authorities"], "EditUser", await (await fetch("/api/" + id)).json());
    new bootstrap.Modal(document.getElementById("editModal")).show()
    document.getElementById("Edit").addEventListener("submit", handleEditForm)
}

async function handleEditForm(event) {
    event.preventDefault()
    let userMap = mapFromTarget(event.target)
    let response = await editUser(userMap)
    if (response.ok) {
        document.getElementById("Edit").removeEventListener("submit", handleEditForm)
        closeOneModal("editModal")
        clearForm()
    } else {
        let errors = await response.json()
        fillHTML(["username", "password", "surname", "name", "patronymic", "birthDate"], "EditUserErrors", errors, "<br>")
    }
}

async function handleDeleteModal(id) {
    fillForm(["id", "username", "surname", "name", "patronymic", "birthDate", "authorities"], "DeleteUser", await (await fetch("/api/" + id)).json());
    new bootstrap.Modal(document.getElementById("deleteModal")).show()
    document.getElementById("Delete").addEventListener("submit", handleDeleteForm)
}

async function handleDeleteForm(event) {
    event.preventDefault()
    let userMap = mapFromTarget(event.target)
    await deleteUserById(userMap)
    document.getElementById("row" + userMap.get("id")).remove()
    document.getElementById("Delete").removeEventListener("submit", handleDeleteForm)
    closeOneModal("deleteModal")
}

function mapFromTarget(target) {
    const {elements} = target
    const map = new Map()
    Array.from(elements)
        .filter(item => !!item.name)
        .forEach(element => {
            const {name, type} = element
            const value = type === 'select-multiple'
                ? Array.from(element.selectedOptions).map(option => option.value)
                : element.value
            map.set(name, value)
        })
    return map
}

function fieldParser(field, separator) {
    return field === undefined ? "" : (Array.isArray(field) ? field.join(separator) : field)
}

function fillHTML(fields, tag, json, separator) {
    for (const field of fields) {
        document.getElementById(field + tag).innerHTML = fieldParser(json[field], separator)
    }
}

function fillForm(fields, tag, json) {
    for (const field of fields) {
        if (field !== "authorities") {
            document.getElementById(field + tag).setAttribute("value", json[field])
        } else {
            Array.from(document.getElementById(field + tag).options).forEach(option => option.selected = json["authorities"].includes(option.value))
        }
    }
}

function fillTable(table, allUsers) {
    table.innerHTML = ""
    for (const user of allUsers) {
        appendToTable(table, user)
    }
}

function appendToTable(table, user) {
    let row = table.insertRow()
    row.setAttribute("id", "row" + user["id"])
    for (const field of ["id", "username", "surname", "name", "patronymic", "birthDate", "authorities"]) {
        row.insertCell().innerHTML = fieldParser(user[field], " ")
    }
    row.insertCell().innerHTML = "<button type=\"button\" class=\"btn btn-primary\" onclick=\"handleEditModal(" + user["id"] + ")\">Редактировать</button>"
    row.insertCell().innerHTML = "<button type=\"button\" class=\"btn btn-danger\" onclick=\"handleDeleteModal(" + user["id"] + ")\">Удалить</button>"
}

function editRow(user) {
    let row = document.getElementById("row" + user.get("id"))
    let fields = ["username", "surname", "name", "patronymic", "birthDate", "authorities"]
    for (let i = 0; i < fields.length; i++) {
        row.cells[i + 1].innerHTML = fieldParser(user.get(fields[i]), " ")
    }
}

function switchActivation(tags) {
    if (tags[0] !== "") {
        document.getElementById(tags[0] + "-tab").classList.remove("active")
        document.getElementById(tags[0]).classList.remove("show", "active")
    }
    document.getElementById(tags[1] + "-tab").classList.add("active")
    document.getElementById(tags[1]).classList.add("show", "active")
}

function closeOneModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.classList.remove('show');
    modal.setAttribute('aria-hidden', 'true');
    modal.setAttribute('style', 'display: none');
    const modalBackdrops = document.getElementsByClassName('modal-backdrop');
    document.body.removeChild(modalBackdrops[0]);
}

function clearForm() {
    document.getElementById("Edit").reset()
    fillHTML(["username", "password", "surname", "name", "patronymic", "birthDate"], "EditUserErrors", "", "<br>")
}