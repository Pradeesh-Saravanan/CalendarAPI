const API_URL = "http://localhost:8080/CalendarAPI/";

const currentYearElement = document.getElementById("current-year");
const calendarMonths = document.getElementById("calendar-months");
const prevButton = document.getElementById("prev-year");
const nextButton = document.getElementById("next-year");

let currentYear = new Date().getFullYear();
const url = new URLSearchParams(window.location.search);
const user = url.get("user");
const EVENT_FETCH_URL = `http://localhost:8080/CalendarAPI/event/post?user=${user}`;
const EVENT_UPDATE_URL = `http://localhost:8080/CalendarAPI/event/put?user=${user}`;
let calendar_id = 0;
let first_time = false;

fetchCalendars();

fetchEvents();

function logout(){
    window.location.href="login.html";
}
function openRightSidebar() {
    document.getElementById("myRightSidebar").style.width = "350px"; 
    document.getElementById("logoutbtn").style.marginRight = "350px"; 
    document.getElementById("rightbtn").style.marginRight = "350px"; 
    document.getElementById("view").style.marginRight = "350px"; 
}
function closeRightSidebar() {
    document.getElementById("myRightSidebar").style.width = "0";
    document.getElementById("logoutbtn").style.marginRight = "0"; 
    document.getElementById("rightbtn").style.marginRight = "0"; 
    document.getElementById("view").style.marginRight = "0"; 
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
    fetchEvents();
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
        const response = await fetch(`http://localhost:8080/CalendarAPI/calendar/get?user=${user}`,

    );
    const raw = await response.text()
    const data = JSON.parse(raw)
    if(first_time==false){
        changeView(data[0].calendar_name,data[0].calendar_id);
        first_time = true;
    }
    const list = document.getElementById("calendar_list");
    list.innerHTML = ``;
    data.forEach(element=>{
        const row = document.createElement("div");
        row.innerHTML=`<button onClick="changeView('${element.calendar_name}','${element.calendar_id}')" style=" border:none; background-color:transparent; font-size:30px; color:white">${element.calendar_name}</button>
                        <button onClick="editCalendar('${element.calendar_id}','${element.calendar_name}','${element.description}')" style=" border:none;  font-size:15px; ">✎</button>`;
        list.appendChild(row);
    });
    } catch (error) {
        console.error("Error fetching posts:", error.API_URL);
    }
}
async function addEvent(year_in,month_in,day_in,URL){
    event_popupOverlay.style.display = 'flex';
    console.log("url",URL);
    // let package = new Map();

    const year = year_in;
    const day = String(day_in).padStart(2,0);
    const month = String(month_in).padStart(2,0);
    const hour = String(new Date().getHours()).padStart(2,'0');
    const minutes = String(new Date().getMinutes()).padStart(2,'0');
    console.log(`${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`);
    console.log(year,month,day,hour,minutes);
    document.getElementById("event-start-date").value = `${year}-${month}-${day}`;
    document.getElementById("event-start-time").value = `${hour}:${minutes}`;

    document.getElementById("all_day").addEventListener('change',(event)=>{
        if(document.getElementById("all_day").checked){
            document.getElementById("event-end-date").value = `${year}-${month}-${day}`;
            document.getElementById("event-start-time").value = `${hour}:${minutes}`;
            document.getElementById("event-end-time").value = `${23}:${59}`;
            document.getElementById("event-start-time").style.display = 'none';
            document.getElementById("event-end-time").style.display = 'none';
        }
        else{
            document.getElementById("event-start-time").style.display = '';
            document.getElementById("event-end-time").style.display = '';
            // document.getElementById("event-end-time").value = ``;
            // document.getElementById("event-end-date").value = ``;
        }
    });

    const recurrence_type_value = null;
    document.getElementById("repeat").addEventListener('change',(event)=>{
        event.preventDefault();
        if(document.getElementById("repeat").checked){
            document.getElementById("recurrence").style.display = 'block';
                const selectElement = document.getElementById("recurrence_type");
          
                selectElement.addEventListener('change', function(event) {
                    // recurrence_type_value = selectElement.value;
                    // if(selectElement.value==="custom"){
                    //     document.getElementById("custom_recurrence_type_div").style.display = 'block';
                    //     document.getElementById("recurrence_interval_div").style.display = 'block';

                    //     document.getElementById("recurrence_interval").value = 1;

                    //     // document.getElementById("custom_recurrence_type").addEventListener('change',function(event){
                    //     //     const value = document.getElementById("custom_recurrence_type").value;
                    //     //     console.log(value);
                    //     //     switch(value){
                    //     //         case "weekly":
                    //     //             document.getElementById("day_of_week_div").style.display = 'block';
                    //     //             document.getElementById("date_of_month_div").style.display = 'none';
                    //     //             document.getElementById("date_of_month").value = '';
                    //     //             document.getElementById("month_of_year").value = '';
                    //     //             document.getElementById("month_of_year_div").style.display = 'none';
                    //     //             break;
                    //     //         case "monthly":
                    //     //             document.getElementById("date_of_month_div").style.display = 'block';
                    //     //             document.getElementById("month_of_year").value = '';
                    //     //             document.getElementById("day_of_week").value = '';
                    //     //             document.getElementById("day_of_week_div").style.display = 'none';
                    //     //             document.getElementById("month_of_year_div").style.display = 'none';
                    //     //             break;
                    //     //         case "yearly":
                    //     //             document.getElementById("date_of_month").value = '';
                    //     //             document.getElementById("day_of_week").value = '';
                    //     //             document.getElementById("month_of_year_div").style.display = 'block';
                    //     //             document.getElementById("date_of_month_div").style.display = 'none';
                    //     //             document.getElementById("day_of_week_div").style.display = 'none';
                    //     //             break;
                    //     //         case "daily":
                    //     //             document.getElementById("day_of_week").value = '';
                    //     //             document.getElementById("date_of_month").value = '';
                    //     //             document.getElementById("month_of_year").value = '';
                    //     //             document.getElementById("month_of_year_div").style.display = 'none';
                    //     //             document.getElementById("date_of_month_div").style.display = 'none';
                    //     //             document.getElementById("day_of_week_div").style.display = 'none';
                    //     //             break;
                    //     //     }
                    //     // });
                    // }
                    // else{
                    //     document.getElementById("custom_recurrence_type_div").style.display = 'none';
                    //     document.getElementById("recurrence_interval_div").style.display = 'none';
                    // }
                    if(selectElement.value ==="daily"){
                        const parent = document.getElementById("recurrence");

                        const select = document.createElement("select");
                        select.id = "recurrence_type_custom";
                        select.classList.add("popupTextBoxFirst");

                        let option = document.createElement("option");
                        
                        const defaultString = "Every Day";
                        option.text = defaultString;
                        option.value = 1;
                        select.appendChild(option);
                        
                        for(let i=2;i<7;i++){
                            let option = document.createElement("option");
                            option.text = "Every "+i+" days";
                            option.value = i;
                            select.appendChild(option);
                        }

                        let optionFinal = document.createElement("option");
                        optionFinal.text = "Custom";
                        optionFinal.value = "custom";
                        select.appendChild(optionFinal);

                        select.addEventListener("change",()=>{
                            if(document.getElementById("recurrence_type").value==="daily" && select.value==="custom"){
                                const parent = document.getElementById("recurrence");

                                const labelOn = document.createElement("label");
                                labelOn.textContent = "Every";
                                parent.appendChild(document.createElement("br"));
                                parent.appendChild(labelOn);

                                const customDay = document.createElement("input");
                                customDay.type = "number";
                                customDay.classList.add("popupTextBox");
                                customDay.id = "recurrence_type_custom_day";
                                // option.appendChild(editable);
                                const labelEnd = document.createElement("label");
                                labelEnd.textContent = "days";
                                
                                parent.appendChild(customDay);
                                parent.appendChild(labelEnd);
                            }
                        });
                        console.log(select);
                        parent.appendChild(select);

                    }
                    else if(selectElement.value ==="weekly"){
                        const parent = document.getElementById("recurrence");

                        const select = document.createElement("select");
                        select.id = "recurence_type_custom";
                        select.classList.add("popupTextBoxFirst");

                        let option = document.createElement("option");

                        
                        const defaultString = "Every Week";
                        option.text = defaultString;
                        option.value = 1;
                        select.appendChild(option);
                        
                        for(let i=2;i<7;i++){
                            let option = document.createElement("option");
                            option.text = "Every "+i+" weeks";
                            option.value = i;
                            select.appendChild(option);
                        }

                        let optionFinal = document.createElement("option");
                        optionFinal.text = "Custom";
                        optionFinal.value = "custom";
                        select.appendChild(optionFinal);

                        parent.appendChild(select);

                        
                        const selectDay = document.createElement("select");
                        selectDay.id = "recurence_type_custom_day";
                        selectDay.classList.add("popupTextBoxFirst");
                        
                        const labelOn = document.createElement("label");
                        labelOn.textContent = "On";
                        parent.appendChild(labelOn);

                        let i=0;
                        ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'].forEach(element=>{
                            const option = document.createElement("option");
                            option.text = element;
                            option.value = i;
                            selectDay.appendChild(option);
                            i++;
                        });

                        parent.appendChild(selectDay);
                        // console.log(parent);
                    }

                });
                console.log(document.getElementById("recurrence_type_custom"));
                // showCustomOption();
            //     document.addEventListener("DOMContentLoaded",()=>{
                    
            // });
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


    
    document.getElementById("event_form_button").addEventListener('click',async (event)=>{
        event.preventDefault();
        let final_type= "";
        const title = document.getElementById("event_name").value;
        const description = document.getElementById("event_description").value;

        // let start_time = new Date(document.getElementById("event-start-date").value+"T"+document.getElementById("event-start-time").value+":00.000Z").toISOString();
        // let end_time = new Date(document.getElementById("event-end-date").value+"T"+document.getElementById("event-end-time").value+":00.000Z").toISOString();
        const startDateInput = document.getElementById("event-start-date");
        const endDateInput = document.getElementById("event-end-date");
        const startTimeInput = document.getElementById("event-start-time");
        const endTimeInput = document.getElementById("event-end-time");

        // Ensure dates are correctly formatted
        const start_time = `${startDateInput.value} ${startTimeInput.value}:00`;
        const end_time = `${endDateInput.value} ${endTimeInput.value}:00`;

        // Create Date objects from the formatted strings
        // const start_time = new Date(startDateString);
        // const end_time = new Date(endDateString);

        console.log(document.getElementById("custom_recurrence_type").value);
        if(document.getElementById("recurrence_type").value==="custom"){
            final_type = `custom ${document.getElementById("custom_recurrence_type").value}`;
        }
        else{
            final_type =document.getElementById("recurrence_type").value;
        }
        let all_day ="false";
        if(document.getElementById("all_day").checked){
            all_day = "true";
        }
        let repeat = "false";
        if(document.getElementById("repeat").checked){
            repeat="true";
        }
        if(!title ||
            !description ||
            !start_time
        ){
            alert("Fill all fields");
            return;
        }
        const package = {
            calendar_id:calendar_id,
            title:title,
            description:description,
            all_day:all_day,
            start_time:start_time,
            end_time:end_time,
            repeat:repeat,
            recurrence_type:final_type,
            recurrence_interval:document.getElementById("recurrence_interval").value,
            day_of_week :document.getElementById("day_of_week").value,
            date_of_month:document.getElementById("date_of_month").value,
            month_of_year:document.getElementById("month_of_year").value
        }
        console.log("package: ",package);
        try{
            const response = await fetch(URL,
                {
                    method:"POST",
                headers:{
                    "Content-Type":"application/json"
                },
                body:JSON.stringify(package),
                }
            );
            const raw = await response.text();
            const data = JSON.parse(raw);
            console.log(data.status);
            console.log(data.message);
            if (response.ok || data.status === "success") {
                document.getElementById("event_name").value= '';
                document.getElementById("event_description").value= '';
                document.getElementById("event-start-date").value= '';
                document.getElementById("event-end-date").value= '';
                document.getElementById("event-start-time").value= '';
                document.getElementById("event-end-time").value= '';
                document.getElementById("repeat").checked= false;
                document.getElementById("all_day").checked= false;
                document.getElementById("custom_recurrence_type").value= 'daily';
                document.getElementById("recurrence").style.display = 'none';
                document.getElementById("custom_recurrence_type_div").style.display = 'none';
                document.getElementById("recurrence_interval_div").style.display = 'none';
                document.getElementById("date_of_month").style.display = 'none';
                document.getElementById("month_of_year").style.display = 'none';
                document.getElementById("day_of_week").style.display = 'none';
                event_popupOverlay.style.display = 'none';
                fetchCalendars();
                fetchEvents(); // Add this line
            } else {
                throw new Error(data.message || "Failed. Please try again.");
            }
        }
        catch(error){
            console.log(error);
        }
    });
}

function showCustomOption(){
    document.getElementById("recurrence_type_custom").addEventListener("change",()=>{
        if(document.getElementById("recurrence_type").value==="daily" && document.getElementById("recurrence_type_custom")==="custom"){
            const parent = document.getElementById("recurrece_type_custom");
            parent.removeChild(parent.lastElementChild);

            const option = document.createElement("option");
            
            const editable = document.createElement("input");

            option.appendChild(editable);

            parent.appendChild(option);
        }
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
            const response = await fetch(`http://localhost:8080/CalendarAPI/calendar/put?user=${user}`,
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
                alert("inserted successfully");
                event_popupOverlay.style.display = 'none';
            } else {
                throw new Error(data.message || "Failed. Please try again.");
            }
        }
        catch(error){
            console.log(error);
        }
    }
  }

  let eventsByDate = {}; // This will store events grouped by date

async function fetchEvents() {
    try {
        const response = await fetch(`${API_URL}event/get?user=${user}&calendar_id=${calendar_id}`);
        const raw = await response.text();
        // alert(raw);
        const data = JSON.parse(raw);
        const list = document.getElementById("event_list");
        list.innerHTML = ``;
        data.forEach(element=>{
            const row = document.createElement("div");
            row.innerHTML=`<button  class="rightpanelBtn" onClick="editEvent('${element.title}','${element.start_time}')">${element.title}</button><br><button>${element.start_time}</button>
                            <button onClick = "deleteEvent('${element.title}')">X</button>`;
            list.appendChild(row);
        });
        // Reset the events storage
        eventsByDate = {};
        
        // Group events by date
        data.forEach(event => {
            // Convert to local date string (YYYY-MM-DD format)
            const startDate = new Date(event.start_time);
            const dateKey = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}`;
            
            if (!eventsByDate[dateKey]) {
                eventsByDate[dateKey] = [];
            }
            eventsByDate[dateKey].push(event);
        });
        
        // Re-render the calendar to show events
        render(currentYear);
    } catch (error) {
        console.error("Error fetching events:", error);
    }
}
async function editEvent(title,start_time){
    const startDate = new Date(start_time);
    const dateKey = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}`;
    

    const event = eventsByDate[dateKey][0];

    const endDate = new Date(event.end_time);
    const endDateTime = `${endDate.getFullYear()}-${String(endDate.getMonth() + 1).padStart(2, '0')}-${String(endDate.getDate()).padStart(2, '0')}`;

    const startTime = `${startDate.getHours()}:${startDate.getMinutes()}`
    const endTime = `${endDate.getHours()}:${endDate.getMinutes()}`
    console.log(event);
 
    document.getElementById("event_name").value= title;
    document.getElementById("event_description").value= event.description;
    document.getElementById("event-start-date").value= dateKey;
    document.getElementById("event-end-date").value= endDateTime;
    document.getElementById("event-start-time").value= startTime;
    document.getElementById("event-end-time").value= endTime;
    document.getElementById("repeat").checked= event.repeat;
    document.getElementById("all_day").checked= event.all_day;
    document.getElementById("custom_recurrence_type").value= event.recurrence_type;

    event_popupOverlay.style.display = 'flex';
    document.getElementById("event_form_button").innerText = "Edit";
    document.getElementById("event_form_button").addEventListener('click',async function(){
        // addEvent(startDate.getFullYear(),startDate.getMonth(),startDate.getDate(),EVENT_UPDATE_URL);
        let final_type= "";
        const title = document.getElementById("event_name").value;
        const description = document.getElementById("event_description").value;

        // let start_time = new Date(document.getElementById("event-start-date").value+"T"+document.getElementById("event-start-time").value+":00.000Z").toISOString();
        // let end_time = new Date(document.getElementById("event-end-date").value+"T"+document.getElementById("event-end-time").value+":00.000Z").toISOString();
        const startDateInput = document.getElementById("event-start-date");
        const endDateInput = document.getElementById("event-end-date");
        const startTimeInput = document.getElementById("event-start-time");
        const endTimeInput = document.getElementById("event-end-time");

        // Ensure dates are correctly formatted
        const start_time = `${startDateInput.value} ${startTimeInput.value}:00`;
        const end_time = `${endDateInput.value} ${endTimeInput.value}:00`;

        // Create Date objects from the formatted strings
        // const start_time = new Date(startDateString);
        // const end_time = new Date(endDateString);

        console.log(document.getElementById("custom_recurrence_type").value);
        if(document.getElementById("recurrence_type").value==="custom"){
            final_type = `custom ${document.getElementById("custom_recurrence_type").value}`;
        }
        else{
            final_type =document.getElementById("recurrence_type").value;
        }
        let all_day ="false";
        if(document.getElementById("all_day").checked){
            all_day = "true";
        }
        let repeat = "false";
        if(document.getElementById("repeat").checked){
            repeat="true";
        }
        if(!title ||
            !description ||
            !start_time
        ){
            alert("Fill all fields");
            return;
        }
        const package = {
            calendar_id:calendar_id,
            title:title,
            description:description,
            all_day:all_day,
            start_time:start_time,
            end_time:end_time,
            repeat:repeat,
            recurrence_type:final_type,
            recurrence_interval:document.getElementById("recurrence_interval").value,
            day_of_week :document.getElementById("day_of_week").value,
            date_of_month:document.getElementById("date_of_month").value,
            month_of_year:document.getElementById("month_of_year").value
        }
        console.log("package: ",package);
        try{
            const response = await fetch(EVENT_UPDATE_URL,
                {
                    method:"POST",
                headers:{
                    "Content-Type":"application/json"
                },
                body:JSON.stringify(package),
                }
            );
            const raw = await response.text();
            const data = JSON.parse(raw);
            console.log(data.status);
            console.log(data.message);
            if (response.ok || data.status === "success") {
                document.getElementById("event_name").value= '';
                document.getElementById("event_description").value= '';
                document.getElementById("event-start-date").value= '';
                document.getElementById("event-end-date").value= '';
                document.getElementById("event-start-time").value= '';
                document.getElementById("event-end-time").value= '';
                document.getElementById("repeat").checked= false;
                document.getElementById("all_day").checked= false;
                document.getElementById("custom_recurrence_type").value= 'daily';
                document.getElementById("recurrence").style.display = 'none';
                document.getElementById("custom_recurrence_type_div").style.display = 'none';
                document.getElementById("recurrence_interval_div").style.display = 'none';
                document.getElementById("date_of_month").style.display = 'none';
                document.getElementById("month_of_year").style.display = 'none';
                document.getElementById("day_of_week").style.display = 'none';
                event_popupOverlay.style.display = 'none';
                fetchCalendars();
                fetchEvents(); // Add this line
            } else {
                throw new Error(data.message || "Failed. Please try again.");
            }
        }
        catch(error){
            console.log(error);
        }
    });
    
}

async function deleteEvent(title){
    try{
        const response = await fetch(`http://localhost:8080/CalendarAPI/event/delete?user=${user}&name=${title}`);
        const raw = await response.text();
        const data = JSON.parse(raw);
        console.log(data.status);
        console.log(data.message);
        if (data.status === "success") {
            alert("deleted successfully");
            window.location.reload();
            // fetchEvents();
        } else {
            throw new Error(data.message || "Failed. Please try again.");
        }
    }
    catch(error){
        console.log(error);
    }
}
function render(year) {
    currentYearElement.textContent = year;
    calendarMonths.innerHTML = "";

    for (let month = 0; month < 12; month++) {
        const monthElement = document.createElement('div');
        monthElement.classList.add('month');

        const weekDaysElement = document.createElement('div');
        weekDaysElement.classList.add('weekdays');

        ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].forEach(day => {
            const dayElement = document.createElement('div');
            dayElement.textContent = day;
            weekDaysElement.appendChild(dayElement);
        });
        monthElement.appendChild(weekDaysElement);

        const daysElement = document.createElement('div');
        daysElement.classList.add('days');

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const daysInMonth = lastDay.getDate();
        const startDay = firstDay.getDay();

        for (let i = 0; i < startDay; i++) {
            const emptyDay = document.createElement('div');
            daysElement.appendChild(emptyDay);
        }

        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = document.createElement('div');
            dayElement.onclick = function() {
                addEvent(year, month + 1, day,EVENT_FETCH_URL);
            };
            dayElement.textContent = day;
            
            // Create a date key for this day
            const dateKey = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            
            // Check if there are events for this day
            if (eventsByDate[dateKey]) {
                const eventsContainer = document.createElement('div');
                eventsContainer.className = 'day-events';
                
                // Limit to showing 2-3 events to prevent overflow
                eventsByDate[dateKey].slice(0, 2).forEach(event => {
                    const eventElement = document.createElement('div');
                    eventElement.className = 'event-dot';
                    eventElement.title = `${event.title}\n${event.description}`;
                    eventsContainer.appendChild(eventElement);
                });
                
                // If there are more events, show a "+ more" indicator
                if (eventsByDate[dateKey].length > 2) {
                    const moreElement = document.createElement('div');
                    moreElement.className = 'event-more';
                    moreElement.textContent = `+${eventsByDate[dateKey].length - 2} more`;
                    moreElement.title = `${eventsByDate[dateKey].length - 2} more events`;
                    eventsContainer.appendChild(moreElement);
                }
                
                dayElement.appendChild(eventsContainer);
            }
            
            daysElement.appendChild(dayElement);
        }

        monthElement.appendChild(daysElement);
        calendarMonths.appendChild(monthElement);
    }
}
prevButton.addEventListener('click',()=>{
    currentYear--;
    render(currentYear);
    fetchCalendars();
    fetchEvents();
});

nextButton.addEventListener('click',()=>{
    currentYear++;
    render(currentYear);
    fetchCalendars();
    fetchEvents();
})

render(currentYear);