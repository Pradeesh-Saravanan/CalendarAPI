const loginForm = document.getElementById("login-form");
        const errorMessage = document.getElementById("error-message");
        const submitButton = document.getElementById("submit-button");

        // onload();
        async function onload() {
            try {
                const response = await fetch("http://localhost:8080/StorageAPI_Struts/login/get", {
                    method: "GET",
                    credentials: "include", 
                });
                const raw = await response.text();
                const data = JSON.parse(raw);
                console.log(data.status);
                if (response.ok || data.status === "success") {
                    console.log(data.user);
                    window.location.href = `dashboard.html?user=${data.user}`;
                }
            } catch (error) {
                // showError(error.message);
            }
        }
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const user = document.getElementById("username").value.trim();
            const passKey = document.getElementById("password").value.trim();

            if (!user || !passKey) {
                showError("Please fill in all fields.");
                return;
            }

            const credentials = {
                username: user,
                password: passKey
            };
            console.log("login running....");
            submitButton.disabled = true;
            errorMessage.textContent = "";

            try {
                const response = await fetch("http://localhost:8080/CalendarAPI/login/post", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include", 
                    body: JSON.stringify(credentials)
                });
                const raw = await response.text();
                const data = JSON.parse(raw);
                console.log(data.status);
                // console.log(data.message);
                if (response.ok || data.status === "success") {
                    // console.log(user);
                    // alert(data.token);
                    window.location.href = `calendar.html?user=${user}`;
                } else {
                    throw new Error(data.message || "Login failed. Please try again.");
                }
            } catch (error) {
                console.log("button event");
                showError(error.message);
            } finally {
                submitButton.disabled = false;
            }
        });

        function showError(message) {
            errorMessage.textContent = message;
            console.log(message);
        }