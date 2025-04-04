async function login(event){
    event.preventDefault();  
    const user = document.getElementById("username").value;
    const passKey = document.getElementById("password").value;
    if (!user || !passKey) {
        alert("Fill all fields");
        return;
    }
    const name = {
        username :user,
        password:passKey
    }

    try {
        const response = await fetch("http://localhost:8080/CalendarAPI/register", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(name)
        });
        const raw = await response.text(); 
        const data = JSON.parse(raw);
        console.log(data); 
        if(data.status == "success"){
            window.location.href="login.html";
        }
        if(data.status =='failed'){
            alert(data.message);
            return;
        }
    } catch (error) {
        console.error("Error:", error);
    }
}   