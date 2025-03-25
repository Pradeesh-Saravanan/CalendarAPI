const API_URL = "http://localhost:8080/CalendarAPI/";

const currentYearElement = document.getElementById("current-year");
const calendarMonths = document.getElementById("calendar-months");
const prevButton = document.getElementById("prev-year");
const nextButton = document.getElementById("next-year");

let currentYear = new Date().getFullYear();
const url = new URLSearchParams(window.location.search);
const user = url.get("user");

const calendar_id = 0;

fetchCalendars();


function logout(){
    window.location.href="login.html";
}

function openSidebar() {
    document.getElementById("mySidebar").style.width = "250px"; 
    document.getElementById("view").style.marginLeft = "250px"; 
    document.getElementById("menu").style.marginLeft = "250px"; 
    document.getElementById("tab").style.marginLeft = "250px"; 
  }
  
  function closeSidebar() {
    document.getElementById("mySidebar").style.width = "0";
    document.getElementById("view").style.marginLeft= "0";
    document.getElementById("tab").style.marginLeft= "0";
    document.getElementById("menu").style.marginLeft= "0";
  }

  const openPopupButton = document.getElementById('create');
  const closePopupButton = document.getElementById('closePopupButton');
  const event_closePopupButton = document.getElementById('event_closePopupButton');
  const popupOverlay = document.getElementById('Overlay');
  const event_popupOverlay = document.getElementById('event_overlay');
  const buttonText = document.getElementById("addbutton");

  openPopupButton.addEventListener('click', (event) => {
      popupOverlay.style.display = 'flex';
  });

  closePopupButton.addEventListener('click', () => {
      popupOverlay.style.display = 'none';
  });

  event_closePopupButton.addEventListener('click', () => {
      event_popupOverlay.style.display = 'none';
  });

  window.addEventListener('click', (event) => {
      if (event.target === popupOverlay ) {
          popupOverlay.style.display = 'none';
      }
      else if(event.target === event_popupOverlay){
          event_popupOverlay.style.display = 'none';
      }
  });

  function changeView(name,id){
    document.getElementById("calendar_name_display").innerText = name;
    calendar_id = id;
  }

  async function addCalendar(){

    if(!document.getElementById("calendar_name").value || !document.getElementById("description").value){
        alert("fill all fields");
    }
    const calendar = {
        calendar_name:document.getElementById("calendar_name").value,
        description:document.getElementById("description").value
    };
    console.log(calendar);  
    try{
        const response = await fetch(`http://localhost:8080/CalendarAPI/calendar/post?user=root`,
            {
                method:"POST",
               headers:{
                "Content-Type":"application/json"
               },
               body:JSON.stringify(calendar)
            }
        );
        const raw = await response.text();
        const data = JSON.parse(raw);
        console.log(data.status);
        if (response.ok || data.status === "success") {
           fetchCalendars();
        } else {
            throw new Error(data.message || "Failed. Please try again.");
        }
    }
    catch(error){
        console.log(error);
    }
  }
  
  async function fetchCalendars() {
    // alert("authenticated");
    try {
        const response = await fetch(`http://localhost:8080/CalendarAPI/calendar/get?user=root`,

    );
    const list = document.getElementById("calendar_list");
    list.innerHTML = ``;
    const raw = await response.text()
    const data = JSON.parse(raw)
    data.forEach(element=>{
        const row = document.createElement("div");
        row.innerHTML=`<button onClick="changeView('${element.calendar_name}','${element.calendar_id}')" style=" border:none; background-color:transparent; font-size:30px; color:white">${element.calendar_name}</button>
                        <button onClick="editCalendar('${element.calendar_id}','${element.calendar_name}','${element.description}')" style=" border:none; background-color:transparent; font-size:30px; color:white">✎</button>`;
        list.appendChild(row);
    });
    } catch (error) {
        console.error("Error fetching posts:", error.API_URL);
    }
}

async function addEvent(year,month,day){
    event_popupOverlay.style.display = 'flex';

    // let package = new Map();
    
    const title = document.getElementById("event_title");
    const description = document.getElementById("event_description");
    
    
    day = String(day).padStart(2,0);
    month = String(month).padStart(2,0);
    const hour = new Date().getHours();
    const minutes = new Date().getMinutes();
    console.log(year,month,day,hour,minutes);
    document.getElementById("event-start-date").value = `${year}-${month}-${day}`;
    document.getElementById("event-start-time").value = `${hour}:${minutes}`;

    document.getElementById("all_day").addEventListener('change',(event)=>{
        if(document.getElementById("all_day").checked){
            document.getElementById("event-end-date").value = `${year}-${month}-${day}`;
            // document.getElementById("event-start-time").value = `${hour}:${minutes}`;
            // document.getElementById("event-end-time").value = `${23}:${59}`;
            document.getElementById("event-start-time").style.display = 'none';
            document.getElementById("event-end-time").style.display = 'none';
        }
        else{
            document.getElementById("event-start-time").style.display = '';
            document.getElementById("event-end-time").style.display = '';
            document.getElementById("event-end-time").value = ``;
            document.getElementById("event-end-date").value = ``;
        }
    });

    document.getElementById("repeat").addEventListener('change',(event)=>{
        event.preventDefault();
        if(document.getElementById("repeat").checked){
            document.getElementById("recurrence").style.display = 'block';
                const selectElement = document.getElementById("recurrence_type");
          
                selectElement.addEventListener('change', function(event) {
                    if(selectElement.value==="custom"){
                        document.getElementById("custom_recurrence_type_div").style.display = 'block';
                        document.getElementById("recurrence_interval_div").style.display = 'block';

                        document.getElementById("recurrence_interval").value = 1;

                        document.getElementById("custom_recurrence_type").addEventListener('change',function(event){
                            const value = document.getElementById("custom_recurrence_type").value;
                            console.log(value);
                            switch(value){
                                case "weekly":
                                    document.getElementById("day_of_week_div").style.display = 'block';
                                    document.getElementById("date_of_month_div").style.display = 'none';
                                    document.getElementById("month_of_year_div").style.display = 'none';
                                    break;
                                case "monthly":
                                    document.getElementById("date_of_month_div").style.display = 'block';
                                    document.getElementById("day_of_week_div").style.display = 'none';
                                    document.getElementById("month_of_year_div").style.display = 'none';
                                    break;
                                case "yearly":
                                    document.getElementById("month_of_year_div").style.display = 'block';
                                    document.getElementById("date_of_month_div").style.display = 'none';
                                    document.getElementById("day_of_week_div").style.display = 'none';
                                    break;
                                case "daily":
                                    document.getElementById("month_of_year_div").style.display = 'none';
                                    document.getElementById("date_of_month_div").style.display = 'none';
                                    document.getElementById("day_of_week_div").style.display = 'none';
                                    break;
                            }
                        });
                    }
                    else{
                        document.getElementById("custom_recurrence_type_div").style.display = 'none';
                        document.getElementById("recurrence_interval_div").style.display = 'none';
                    }

                });
        }else{
            document.getElementById("recurrence_type").value = "daily";
            document.getElementById("month_of_year_div").style.display = 'none';
            document.getElementById("date_of_month_div").style.display = 'none';
            document.getElementById("day_of_week_div").style.display = 'none';
            document.getElementById("custom_recurrence_type_div").style.display = 'none';
            document.getElementById("recurrence_interval_div").style.display = 'none';
            document.getElementById("recurrence").style.display = 'none';
        }
    });


    document.getElementById("event_form_button").addEventListener('click',(event)=>{
        const final_type= "";
        if(document.getElementById("recurrence_type")==="custom"){
            final_type = document.getElementById("custom_recurrence_type").value;
        }
        else{
            final_type =document.getElementById("recurrence_type").value;
        }
    
        const package = {
            calendar_id:calendar_id,
            title:title,
            description:description,
            all_day:document.getElementById("all_day").value,
            start_time:`${document.getElementById("event-start-date").value} ${document.getElementById("event-start-time").value}`,
            end_time:`${document.getElementById("event-end-date").value} ${document.getElementById("event-end-time").value}`,
            repeat:document.getElementById("repeat").value,
            recurrence_type:final_type,
            recurrence_interval:document.getElementById("recurrence_interval").value,
            day_of_week :document.getElementById("day_of_week").value,
            date_of_month:document.getElementById("date_of_month").value,
            month_of_year:document.getElementById("month_of_year").value
        }
        console.log("package: ",package);
    });
}

async function editCalendar(id,name,description){

    console.log(id,name,description);
    popupOverlay.style.display = 'flex';
    document.getElementById('calendar_name').value  = name;
    document.getElementById('description').value  = description;
    document.getElementById('form_button').innerText = "Edit";
    document.getElementById('form_title').innerText = "Edit Calendar";

    document.getElementById('form_button').onclick = async function () {
        if(!document.getElementById("calendar_name").value || !document.getElementById("description").value){
            alert("fill all fields");
        }
        const calendar = {
            calendar_name:document.getElementById("calendar_name").value,
            description:document.getElementById("description").value,
            calendar_id :id
        };
        console.log(calendar);  
        try{
            const response = await fetch(`http://localhost:8080/CalendarAPI/calendar/put?user=root`,
                {
                    method:"PUT",
                headers:{
                    "Content-Type":"application/json"
                },
                body:JSON.stringify(calendar)
                }
            );
            const raw = await response.text();
            const data = JSON.parse(raw);
            console.log(data.status);
            console.log(data.message);
            if (response.ok || data.status === "success") {
                fetchCalendars();
                popupOverlay.style.display = 'none';
            } else {
                throw new Error(data.message || "Failed. Please try again.");
            }
        }
        catch(error){
            console.log(error);
        }
    }
  }
function render(year){
    currentYearElement.textContent = year;

    calendarMonths.innerHTML = "";

    for(let month = 0;month<12;month++){
        const monthElement = document.createElement('div');
        monthElement.classList.add('month');

        const weekDaysElement = document.createElement('div');
        weekDaysElement.classList.add('weekdays');

        ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'].forEach(day=>{
            const dayElement = document.createElement('div');
            dayElement.textContent = day;
            weekDaysElement.appendChild(dayElement);
        });
        monthElement.appendChild(weekDaysElement);

        const daysElement = document.createElement('div');
        daysElement.classList.add('days');

        const firstDay = new Date(year,month,1);
        const lastDay = new Date(year,month+1,0);
        const daysInMonth = lastDay.getDate();
        const startDay = firstDay.getDay();

        for(let i=0;i<startDay;i++){
            const emptyDay = document.createElement('div');
            daysElement.appendChild(emptyDay);
        }

        for(let day = 1;day<=daysInMonth;day++){
            const dayElement = document.createElement('div');
            dayElement.onclick = function(){
                addEvent(year,month+1,day);
            };
            dayElement.textContent = day;
            daysElement.appendChild(dayElement);
        }

        monthElement.appendChild(daysElement);
        calendarMonths.appendChild(monthElement);
    }
}

prevButton.addEventListener('click',()=>{
    currentYear--;
    render(currentYear);
});

nextButton.addEventListener('click',()=>{
    currentYear++;
    render(currentYear);
})

render(currentYear);