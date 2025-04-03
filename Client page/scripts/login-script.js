const loginForm = document.getElementById("login-form");
        const errorMessage = document.getElementById("error-message");
        const submitButton = document.getElementById("submit-button");

        onload();
        async function onload() {
            const token = localStorage.getItem('token');
            if(token){
                window.location.href="calendar.html";
                return;
            }

            // try {
            //     const response = await fetch("http://localhost:8080/CalendarAPI/secured/login/get", {
            //         method: "GET",
            //         headers:{
            //             "Authorization":`Bearer ${token}`,
            //         } 
            //     });
            //     const raw = await response.text();
            //     const data = JSON.parse(raw);
            //     console.log(data.status);
            //     if (response.ok || data.status === "success") {
            //         // console.log(data.user);
            //         window.location.href = `calendar.html?user=${data.user}`;
            //     }
            // } catch (error) {
            //     // showError(error.message);
            // }
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
                const response = await fetch("http://localhost:8080/CalendarAPI/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(credentials)
                });
                const raw = await response.text();
                const data = JSON.parse(raw);
                console.log(data.status);
                // console.log(data.message);
                if (response.ok || data.status === "success") {
                    // console.log(user);
                    // alert(data.token);
                    if(data.token){
                        localStorage.setItem("token",data.token);
                        window.location.href = `calendar.html`;
                    }
                    else{
                        console.log("empty token")
                    }
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