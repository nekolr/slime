$.ajaxSetup({
    global: true,
    beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', "Bearer " + localStorage.getItem("slime_token"));
    },
    complete: function (xhr) {
        if (xhr.status === 401) {
            window.localStorage.removeItem("slime_token");
            if (window.parent) {
                window.parent.location.href = "/login.html"
            } else {
                window.location.href = "/login.html"
            }
        }
    }
});