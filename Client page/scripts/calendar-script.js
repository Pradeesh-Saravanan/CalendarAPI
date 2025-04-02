const API_URL = "http://localhost:8080/CalendarAPI/";

const currentYearElement = document.getElementById("current-year");
const calendarMonths = document.getElementById("calendar-months");
const prevButton = document.getElementById("prev-year");
const nextButton = document.getElementById("next-year");

let currentYear = new Date().getFullYear();
const url = new URLSearchParams(window.location.search);
const token = localStorage.getItem('token');
const user = url.get("user");
const EVENT_FETCH_URL = `http://localhost:8080/CalendarAPI/event/post?user=${user}`;
const EVENT_UPDATE_URL = `http://localhost:8080/CalendarAPI/event/put?user=${user}`;
let calendar_id = 0;
let first_time = false;

fetchCalendars();

// fetchEvents();

function logout(){
    localStorage.removeItem("token");
    window.location.href="login.html";
}
function openRightSidebar() {
    fetchEvents();
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



  openPopupButton.addEventListener('click',()=>{
      popupOverlay.style.display = 'flex';
  });

  closePopupButton.addEventListener('click',  ()=>{
      popupOverlay.style.display = 'none';
  });

  event_closePopupButton.addEventListener('click',  ()=>{
      event_popupOverlay.style.display = 'none';
  });

  window.addEventListener('click',  (event)=>{
      if (event.target === popupOverlay ) {
          popupOverlay.style.display = 'none';
      }
      else if(event.target === event_popupOverlay){
          event_popupOverlay.style.display = 'none';
      }
  });

  async function changeView(name,id){
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
        const response = await fetch(`http://localhost:8080/CalendarAPI/secured/calendar/post?user=${user}`,
            {
                method:"POST",
               headers:{
                "Content-Type":"application/json"
               },
               body:JSON.stringify(calendar)
            }
        );
        console.log(response.status);
        if(!response.status===401){
            alert("Unauthorized");
            window.location.href="login.html";
        }
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
    if (!token) {
        console.log("No token found");
        window.location.href="login.html";
        return;
    }
    try {
        const response = await fetch(`http://localhost:8080/CalendarAPI/secured/calendar/get?user=${user}`,
            {
                method:"GET",
                headers:{
                    'Authorization':`Bearer ${token}`,
                }
            }
        );
        if(!response.status===401 || !response.ok){
        window.location.href="login.html";
        }
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
        window.location.href="login.html";
        console.error("Error fetching posts:", error);
    }
}

const dateSet = new Set();

function resetMontView(){
    dateSet.clear();
    document.getElementById("day").value = "Monday";
    document.getElementById("week").value = "First";
    const collection = document.querySelectorAll(".monthView div");
    for(let i=0;i<collection.length;i++){
        collection[i].style.backgroundColor = "transparent";
    }
}

function createMonthView(id){
    const parentDiv = document.createElement("div");

    const monthElement = document.createElement("div");
    const viewButton = document.createElement("input");
    viewButton.setAttribute("type","checkbox");
    viewButton.id = "monthViewButton";
    monthElement.classList.add("monthView");
    parentDiv.appendChild(viewButton);
    monthElement.classList.add("monthView");
    const daysElement = document.createElement("div");
    daysElement.classList.add("days");
    for(let day=1;day<=31;day++){
        const dayElement = document.createElement("div");
        // dayElement.onclick= function(){};
        dayElement.textContent =day;
        dayElement.addEventListener("click",()=>{

            dayElement.style.backgroundColor = dayElement.style.backgroundColor==="rgb(196, 217, 255)" ? "transparent" : "rgb(196, 217, 255)";
            if(dateSet.has(day+",")){
                dateSet.delete(day+",");
            }
            else{
                dateSet.add(day+",");
                if(dateSet.size>5){
                    alert("only 5 dates can be included");
                    dateSet.delete(day+",");
                    dayElement.style.backgroundColor = "transparent";
                }
            }
            if(dateSet.size==0){
                document.getElementById(id).textContent = "Select Day(s) ";
            }
            else{
                displayDate(id);
            }
            console.log(dateSet);
            
        });
        daysElement.appendChild(dayElement);
    }
    monthElement.appendChild(daysElement);
    viewButton.addEventListener("click",()=>{
        // weekViewButton.checked = false;
        if(viewButton.checked==true){
            displayDate(id);
            monthElement.style.pointerEvents = "auto";
            monthElement.style.opacity = "1";
            dayDiv.style.pointerEvents = "none";
            dayDiv.style.opacity = "0.5";
            weekViewButton.checked=false;
        }
        else{
            monthElement.style.pointerEvents = "none";
            monthElement.style.opacity = "0.5";
        }
        // monthElement.style.pointerEvents = monthElement.style.pointerEvents === "none"?"auto":"none";
        // monthElement.style.opacity = monthElement.style.opacity==="0.5"?"1":"0.5";
        // dayDiv.style.pointerEvents = "none";
        // dayDiv.style.opacity = "0.5";
    });

    const dayDiv = document.createElement("div");
    
    const week = createSelectElement("week",weekOptions);
    const day = createSelectElement("day",dayOptions);
    dayDiv.appendChild(week);
    dayDiv.appendChild(day);
    week.addEventListener("change",()=>{
        displayDay(id);
    });
    day.addEventListener("change",()=>{
        displayDay(id);
    });

    const weekViewButton = document.createElement("input");
    weekViewButton.setAttribute("type","checkbox");
    weekViewButton.id = "weekViewButton";
    dayDiv.classList.add("dayView");
    parentDiv.appendChild(monthElement);
    parentDiv.appendChild(weekViewButton);

    weekViewButton.addEventListener("click",()=>{
        // viewButton.checked = false;
        // monthElement.style.pointerEvents = "none";
        // monthElement.style.opacity ="0.5";
        // dayDiv.style.pointerEvents = dayDiv.style.pointerEvents==="none"?"auto":"none";
        // dayDiv.style.opacity = dayDiv.style.opacity==="0.5"?"1":"0.5";

        if(weekViewButton.checked==true){
            displayDay(id);
            monthElement.style.pointerEvents = "none";
            monthElement.style.opacity = "0.5";
            dayDiv.style.pointerEvents = "auto";
            dayDiv.style.opacity = "1";
            viewButton.checked = false;
        }
        else{
            dayDiv.style.pointerEvents = "none";
            dayDiv.style.opacity = "0.5";
        }
    });
    
    parentDiv.appendChild(dayDiv);
    return parentDiv;
}
function createSelectElement(id, options) {
    const select = document.createElement('select');
    select.id = id;
    select.className = 'popupTextBox';

    options.forEach(option => {
        const opt = document.createElement('option');
        opt.value = option.value;
        opt.textContent = option.text;
        select.appendChild(opt);
    });

    return select;
}

// Options for the week select
const weekOptions = [
    { value: 'First', text: 'First' },
    { value: 'Second', text: 'Second' },
    { value: 'Third', text: 'Third' },
    { value: 'Fourth', text: 'Fourth' },
    { value: 'Fifth', text: 'Fifth' },
    { value: 'Last', text: 'Last' }
];

// Options for the day select
const dayOptions = [
    { value: 'Monday', text: 'Mon' },
    { value: 'Tuesday', text: 'Tue' },
    { value: 'Wednesday', text: 'Wed' },
    { value: 'Thursday', text: 'Thu' },
    { value: 'Friday', text: 'Fri' },
    { value: 'Saturday', text: 'Sat' },
    { value: 'Sunday', text: 'Sun' },
    { value: 'Day', text: 'Day' }
];

function displayDay(id){
    if(document.getElementById("day").value && document.getElementById("week").value){
        // console.log(document.getElementById("day").value + document.getElementById("week").value);
        document.getElementById(id).textContent  = "On the " +document.getElementById("week").value +" "+ document.getElementById("day").value;
    }
}
function displayDate(id){
    if(dateSet.size>0){
        let dateStr = "";
                dateSet.forEach(date=>{
                    dateStr += date;
                });
        document.getElementById(id).textContent = "On Each "+dateStr;
    }
    else{
        document.getElementById(id).textContent = "Select Day(s) ";
    }
}

const daySet = new Set();
function customDays(){
    const parent = document.getElementById("customDays");

    dayOptions.forEach(option => {
        const opt = document.createElement('div');
        opt.classList.add("popupTextBox");
        // opt.value = option.value;
        opt.textContent = option.value;

        opt.addEventListener("click",async function (event){
            event.preventDefault();
            opt.style.backgroundColor = opt.style.backgroundColor==="rgb(196, 217, 255)" ? "white":"rgb(196, 217, 255)";
            if(daySet.has(opt.textContent+",")){
                daySet.delete(opt.textContent+",");
            }
            else{
                daySet.add(opt.textContent+",");
            }
            document.getElementById("customDays").style.display = 'block';
            displayCustomDay();
        });

        parent.appendChild(opt);
    });
}

function displayCustomDay(){
    if(daySet.size>0){
        let dayStr = "";
                daySet.forEach(day=>{
                    dayStr += day;
                });
        document.getElementById("select_custom_days").textContent = "On Each "+dayStr;
    }
    else{
        document.getElementById("select_custom_days").textContent = "Select Day(s) ";
    }
}
document.getElementById("repeat").addEventListener('change',async function (event){
    event.preventDefault();
    const parent = document.getElementById("recurrence");
    if(document.getElementById("monthView").childElementCount===0){
        document.getElementById("monthView").appendChild(createMonthView("select_days"));
    }
    if(document.getElementById("customDays").childElementCount===0){
        customDays();
    }
    if(document.getElementById("repeat").checked){
        document.getElementById("custom_recurrence_type_div").style.display = 'block';
        document.getElementById("custom_recurrence_type").addEventListener("change",async function (event){

            if(document.getElementById("custom_recurrence_type").value=='daily'){
                document.getElementById("custom_recurrence_type_daily_div").style.display = 'block';
                document.getElementById("custom_recurrence_type_weekly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_monthly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_yearly_div").style.display = 'none';
                document.getElementById("month_of_year_div").style.display = "none";
                document.getElementById("dropdown").style.display = 'none';
                document.getElementById("day_div").style.display = 'none';
            }
            else if(document.getElementById("custom_recurrence_type").value=='weekly'){
                document.getElementById("month_of_year_div").style.display = "none";
                document.getElementById("custom_recurrence_type_weekly_div").style.display = 'block';
                document.getElementById("custom_recurrence_type_daily_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_monthly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_yearly_div").style.display = 'none';
                document.getElementById("dropdown").style.display = 'none';
                document.getElementById("day_div").style.display = 'block';
            }
            else if(document.getElementById("custom_recurrence_type").value=='monthly'){
                // document.getElementById("yearView").innerHTML = ``;
                // var yearView = document.getElementById("yearView");
                // while(yearView.firstChild){
                //     yearView.removeChild(yearView.firstChild);
                // }
                // dateSet.clear();
                document.getElementById("select_days").textContent = "Select Day(s)";
                // document.getElementById("day").value = "Monday";
                // document.getElementById("week").value = "First";
                resetMontView();
                document.getElementById("dropdown").style.display = 'block';
                document.getElementById("month_of_year_div").style.display = "none";
                document.getElementById("custom_recurrence_type_monthly_div").style.display = 'block';
                document.getElementById("custom_recurrence_type_daily_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_weekly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_yearly_div").style.display = 'none';
                document.getElementById("day_div").style.display = 'none';
                // document.getElementById("dropdown").style.display = 'block';
                // document.getElementById("monthView").appendChild(createMonthView("select_days"));
                // document.getElementById("select_days").addEventListener
                // ('click',()=>{
                //     document.getElementById("monthView").style.display =document.getElementById("monthView").style.display === 'block' ? 'none' : 'block';
                // });
            }
            else if(document.getElementById("custom_recurrence_type").value=='yearly'){
                // document.getElementById("monthView").innerHTML = ``;
                // var monthView = document.getElementById("monthView");
                // while(monthView.firstChild){
                //     monthView.removeChild(monthView.firstChild);
                // }
                document.getElementById("select_days").textContent = "Select Day(s)";
                // dateSet.clear();
                // document.getElementById("day").value = "Monday";
                // document.getElementById("week").value = "First";
                resetMontView();
                document.getElementById("dropdown").style.display = 'block';
                document.getElementById("month_of_year_div").style.display = 'block';
                document.getElementById("custom_recurrence_type_yearly_div").style.display = 'block';
                document.getElementById("custom_recurrence_type_daily_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_weekly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_monthly_div").style.display = 'none';
                document.getElementById("day_div").style.display = 'none';
                // document.getElementById("yearView").appendChild(createMonthView("select_days"));
                // document.getElementById("select_days").addEventListener
                // ('click',()=>{
                //     document.getElementById("monthView").style.display =document.getElementById("monthView").style.display === 'block' ? 'none' : 'block';
                // });
                // document.getElementById("day_div").style.display = 'none';
            }
        });
    }else{
        // document.getElementById("recurrence_type").value = "daily";
        // document.getElementById("month_of_year_div").style.display = 'none';
        // document.getElementById("date_of_month_div").style.display = 'none';
        // document.getElementById("day_of_week_div").style.display = 'none';
        document.getElementById("month_of_year_div").style.display = "none";
        document.getElementById("day_div").style.display = 'none';
        document.getElementById("monthView").innerHTML = ``;
        document.getElementById("customDays").innerHTML = ``;
        document.getElementById("dropdown").style.display = "none";
        document.getElementById("custom_recurrence_type_div").style.display = 'none';
        document.getElementById("custom_recurrence_type_yearly_div").style.display = 'none';
        document.getElementById("custom_recurrence_type_daily_div").style.display = 'none';
        document.getElementById("custom_recurrence_type_weekly_div").style.display = 'none';
        document.getElementById("custom_recurrence_type_monthly_div").style.display = 'none';
        // document.getElementById("day_div").style.display = 'none';
        // document.getElementById("recurrence_interval_div").style.display = 'none';
        // document.getElementById("recurrence").style.display = 'none';
    }
    
});
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

    document.getElementById("all_day").addEventListener('change', (event)=>{
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

    

    
    
    document.getElementById("event_form_button").addEventListener('click',async function (event){
        event.preventDefault();
        // let final_type= "";
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

        // console.log(document.getElementById("custom_recurrence_type").value);
        // if(document.getElementById("recurrence_type").value==="custom"){
        //     final_type = `custom ${document.getElementById("custom_recurrence_type").value}`;
        // }
        // else{
        //     final_type =document.getElementById("recurrence_type").value;
        // }
        let all_day ="false";
        if(document.getElementById("all_day").checked){
            all_day = "true";
        }
        let repeat = "false";
        if(document.getElementById("repeat").checked){
            repeat="true";
        }
        if(!title || !description || !start_time){
            alert("Fill all fields");
            return;
        }
        const frequency = document.getElementById("custom_recurrence_type").value;
        let repeatInterval = null;
        let day = null;
        let date = null;
        let week = null;
        let month = null;
        let recurrence_type = null;
        let isMonthView = document.getElementById("monthViewButton").checked;
        let isWeekView = document.getElementById("weekViewButton").checked;
        switch(frequency){
            case "daily":
                recurrence_type = document.getElementById("custom_recurrence_type_daily").value;
                if(recurrence_type=="5"){
                    repeatInterval = document.getElementById("custom_number").value;
                }
                else{
                    repeatInterval = recurrence_type;
                }
                break;
            case "weekly":
                recurrence_type = document.getElementById("custom_recurrence_type_weekly").value;
                if(recurrence_type=="5"){
                    repeatInterval = document.getElementById("custom_number").value;
                }
                else{
                    repeatInterval = recurrence_type;
                }
                let dayStr = "";
                daySet.forEach(day=>{
                    dayStr += day;
                });
                day = dayStr;
                break;
            case "monthly":
                recurrence_type = document.getElementById("custom_recurrence_type_monthly").value;
                if(recurrence_type=="5"){
                    repeatInterval = document.getElementById("custom_number").value;
                }
                else{
                    repeatInterval = recurrence_type;
                }
                if(isMonthView==true){
                    let dateStr = "";
                    dateSet.forEach(date=>{
                        dateStr += date;
                    });
                    date = dateStr;
                }
                else if (isWeekView==true){
                    week = document.getElementById("week").value;
                    day = document.getElementById("day").value;
                }
                break;
            case "yearly":
                repeatInterval = document.getElementById("custom_recurrence_type_yearly").value;
                month = document.getElementById("month_of_year").value;
                if(isMonthView==true){
                    console.log(dateSet);
                    let dateStr = "";
                    dateSet.forEach(date=>{
                        dateStr += date;
                    });
                    date = dateStr;
                }
                else if (isWeekView==true){
                    week = document.getElementById("week").value;
                    day = document.getElementById("day").value;
                }
                break;
        }

        const package = {
            calendarId:calendar_id,
            title:title,
            description:description,
            allDay:all_day,
            startTime:start_time,
            endTime:end_time,
            isRecurring:repeat,
            frequency:frequency,
            repeatInterval:repeatInterval,
            day :day,
            date:date,
            month:month,
            week:week,
            offset:"1"
        }
        console.log("package: ",package);
        // return;
        try{
            const response = await fetch(`http://localhost:8080/CalendarAPI/secured/schedule/post?user=${user}`,
                {
                    method:"POST",
                headers:{
                    "Content-Type":"application/json",
                    "Authorization":`Bearer ${token}`,
                },
                body:JSON.stringify(package),
                }
            );
            document.getElementById("event_form_button").disabled = true;
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

                document.getElementById("custom_recurrence_type_weekly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_daily_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_monthly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_yearly_div").style.display = 'none';
                document.getElementById("dropdown").style.display = 'none';
                document.getElementById("day_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_div").style.display = 'none';

                // document.getElementById("recurrence").style.display = 'none';
                // document.getElementById("recurrence_interval_div").style.display = 'none';
                // document.getElementById("date_of_month").style.display = 'none';
                // document.getElementById("month_of_year").style.display = 'none';
                // document.getElementById("day_of_week").style.display = 'none';
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
        finally{
            document.getElementById("event_form_button").disabled = false;  
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

document.getElementById("custom_recurrence_type_daily").addEventListener("change",()=>{
    if(document.getElementById("custom_recurrence_type_daily").value=="5"){
        document.getElementById("custom_repeat").style.display = document.getElementById("custom_repeat").style.display==="block"?"none":"block";
    }
    else{
        document.getElementById("custom_repeat").style.display= 'none';
    }
});

document.getElementById("custom_recurrence_type_weekly").addEventListener("change",()=>{
    if(document.getElementById("custom_recurrence_type_weekly").value=="5"){
        document.getElementById("custom_repeat").style.display = document.getElementById("custom_repeat").style.display==="block"?"none":"block";
    }
    else{
        document.getElementById("custom_repeat").style.display= 'none';
    }
});
document.getElementById("custom_recurrence_type_monthly").addEventListener("change",()=>{
    if(document.getElementById("custom_recurrence_type_monthly").value=="5"){
        document.getElementById("custom_repeat").style.display = document.getElementById("custom_repeat").style.display==="block"?"none":"block";
    }
    else{
        document.getElementById("custom_repeat").style.display= 'none';
    }
});
document.getElementById("select_days").addEventListener
        ('click',()=>{
            document.getElementById("monthView").style.display =document.getElementById("monthView").style.display === 'block' ? 'none' : 'block';
        });
        document.getElementById("day_div").addEventListener
            ('click',()=>{
                document.getElementById("customDays").style.display =document.getElementById("customDays").style.display === 'block' ? 'none' : 'block';
        });
async function editCalendar(id,name,description){

    console.log(id,name,description);
    popupOverlay.style.display = 'flex';
    document.getElementById('calendar_name').value  = name;
    document.getElementById('description').value  = description;
    document.getElementById('form_button').innerText = "Edit";
    document.getElementById('form_title').innerText = "Edit Calendar";

    document.getElementById('form_button').onclick = async ()=> {
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
            // const response = await fetch(`http://localhost:8080/CalendarAPI/calendar/put?user=${user}`,
            //     {
            //         method:"PUT",
            //     headers:{
            //         "Content-Type":"application/json"
            //     },
            //     body:JSON.stringify(calendar)
            //     }
            // );
            const response = await fetch(`http://localhost:8080/CalendarAPI/secured/calendar/put?user=${user}`,
                {
                    method:"PUT",
                    headers:{
                        "Content-Type":"application/json",
                        'Authorization':`Bearer ${token}`,
                    },
                    body:JSON.stringify(calendar)
                }
            );
            if(!response.status===401 || !response.ok){
            window.location.href="login.html";
            }
            const raw = await response.text();
            const data = JSON.parse(raw);
            console.log(data.status);
            console.log(data.message);
            if (response.ok || data.status === "success") {
                alert("inserted successfully");
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

  let eventsByDate = {}; // This will store events grouped by date

async function fetchEvents() {
    try {
        const package = {
            calendarId:calendar_id,
        };
        const list = document.getElementById("event_list");
        list.innerHTML = ``;
        // const response = await fetch(`${API_URL}event/get?user=${user}&calendar_id=${calendar_id}`);
        const response = await fetch(`http://localhost:8080/CalendarAPI/secured/schedule/get?user=${user}`,
            {
                method:"POST",
                headers:{
                    'Authorization':`Bearer ${token}`,
                },
                body:JSON.stringify(package),
            }
        );

        // fetch(`http://localhost:8080/CalendarAPI/secured/schedule/get?user=${user}`,
        //     {
        //         method:"POST",
        //         headers:{
        //             'Authorization':`Bearer ${token}`,
        //         },
        //         body:JSON.stringify(package),
        //     }
        // ).then(response=>response.json()).then(data=>{
        //     data.forEach(event => {
        //         const row = document.createElement("div");
        //         row.innerHTML=`<button  class="rightpanelBtn" onClick="editEvent('${event.title}','${event.startTime}')">${event.title}</button><br><button>${event.startTime}</button>
        //                         <button onClick = "deleteEvent('${event.schedule_id}')">X</button>`;
        //         list.appendChild(row);
        //         // Convert to local date string (YYYY-MM-DD format)
        //         const startDate = new Date(event.startTime);
        //         const dateKey = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}`;
                
        //         if (!eventsByDate[dateKey]) {
        //             eventsByDate[dateKey] = [];
        //         }
        //         eventsByDate[dateKey].push(event);
        //     });
        //     if(!response.status===401 || !response.ok){
        //         window.location.href="login.html";
        //     }
        // }).catch(error=>console.error(error));
        const raw = await response.text();
        // alert(raw);
        const data = JSON.parse(raw);
        
        // data.forEach(element=>{
            
        // });
        // Reset the events storage
        eventsByDate = {};
        
        // Group events by date
        data.forEach(event => {
            const row = document.createElement("div");
            row.innerHTML=`<button  class="rightpanelBtn" onClick="editEvent('${event.title}','${event.startTime}')">${event.title}</button><br><button>${event.startTime}</button>
                            <button onClick = "deleteEvent('${event.schedule_id}')">X</button>`;
            list.appendChild(row);
            // Convert to local date string (YYYY-MM-DD format)
            const startDate = new Date(event.startTime);
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
    const schedule_id = event.schedule_id;
    document.getElementById("event_name").value= title;
    document.getElementById("event_description").value= event.description;
    document.getElementById("event-start-date").value= dateKey;
    document.getElementById("event-end-date").value= endDateTime;
    document.getElementById("event-start-time").value= startTime;
    document.getElementById("event-end-time").value= endTime;
    document.getElementById("repeat").checked= event.repeatInterval;
    document.getElementById("all_day").checked= event.allDay;
    document.getElementById("custom_recurrence_type").value= event.frequency;
    document.getElementById("custom_recurrence_type_div").style.display= "block";
    switch(event.frequency){
        case "daily":
            document.getElementById("custom_recurrence_type_daily_div").style.display = 'block';
            document.getElementById("custom_recurrence_type_daily").value=event.repeatInterval;
            break;
        // case "weekly":
        //     recurrence_type = document.getElementById("custom_recurrence_type_weekly").value;
        //     if(recurrence_type=="5"){
        //         repeatInterval = document.getElementById("custom_number").value;
        //     }
        //     else{
        //         repeatInterval = recurrence_type;
        //     }
        //     let dayStr = "";
        //     daySet.forEach(day=>{
        //         dayStr += day;
        //     });
        //     day = dayStr;
        //     break;
        // case "monthly":
        //     recurrence_type = document.getElementById("custom_recurrence_type_monthly").value;
        //     if(recurrence_type=="5"){
        //         repeatInterval = document.getElementById("custom_number").value;
        //     }
        //     else{
        //         repeatInterval = recurrence_type;
        //     }
        //     if(isMonthView==true){
        //         let dateStr = "";
        //         dateSet.forEach(date=>{
        //             dateStr += date;
        //         });
        //         date = dateStr;
        //     }
        //     else if (isWeekView==true){
        //         week = document.getElementById("week").value;
        //         day = document.getElementById("day").value;
        //     }
        //     break;
        // case "yearly":
        //     repeatInterval = document.getElementById("custom_recurrence_type_yearly").value;
        //     month = document.getElementById("month_of_year").value;
        //     if(isMonthView==true){
        //         console.log(dateSet);
        //         let dateStr = "";
        //         dateSet.forEach(date=>{
        //             dateStr += date;
        //         });
        //         date = dateStr;
        //     }
        //     else if (isWeekView==true){
        //         week = document.getElementById("week").value;
        //         day = document.getElementById("day").value;
        //     }
        //     break;
    }
    // document.getElementById("day").value= event.day;
    // document.getElementById("date").value= event.date;
    document.getElementById("month_of_year").value= event.month;
    // document.getElementById("week").value= event.week;

    event_popupOverlay.style.display = 'flex';
    document.getElementById("event_form_button").innerText = "Edit";
    // return;
    document.getElementById("all_day").addEventListener('change', (event)=>{
        if(document.getElementById("all_day").checked){
            document.getElementById("event-end-date").value = startDate;
            document.getElementById("event-start-time").value = startTime;
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
    document.getElementById("event_form_button").addEventListener('click',async ()=>{
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

        // console.log(document.getElementById("custom_recurrence_type").value);
        // if(document.getElementById("recurrence_type").value==="custom"){
        //     final_type = `custom ${document.getElementById("custom_recurrence_type").value}`;
        // }
        // else{
        //     final_type =document.getElementById("recurrence_type").value;
        // }
        let all_day ="false";
        if(document.getElementById("all_day").checked){
            all_day = "true";
        }
        let repeat = "false";
        if(document.getElementById("repeat").checked){
            repeat="true";
        }
        if(!title || !description || !start_time){
            alert("Fill all fields");
            return;
        }
        const frequency = document.getElementById("custom_recurrence_type").value;
        let repeatInterval = null;
        let day = null;
        let date = null;
        let week = null;
        let month = null;
        let recurrence_type = null;
        let isMonthView = document.getElementById("monthViewButton").checked;
        let isWeekView = document.getElementById("weekViewButton").checked;
        switch(frequency){
            case "daily":
                recurrence_type = document.getElementById("custom_recurrence_type_daily").value;
                if(recurrence_type=="5"){
                    repeatInterval = document.getElementById("custom_number").value;
                }
                else{
                    repeatInterval = recurrence_type;
                }
                break;
            case "weekly":
                recurrence_type = document.getElementById("custom_recurrence_type_weekly").value;
                if(recurrence_type=="5"){
                    repeatInterval = document.getElementById("custom_number").value;
                }
                else{
                    repeatInterval = recurrence_type;
                }
                let dayStr = "";
                daySet.forEach(day=>{
                    dayStr += day;
                });
                day = dayStr;
                break;
            case "monthly":
                recurrence_type = document.getElementById("custom_recurrence_type_monthly").value;
                if(recurrence_type=="5"){
                    repeatInterval = document.getElementById("custom_number").value;
                }
                else{
                    repeatInterval = recurrence_type;
                }
                if(isMonthView==true){
                    let dateStr = "";
                    dateSet.forEach(date=>{
                        dateStr += date;
                    });
                    date = dateStr;
                }
                else if (isWeekView==true){
                    week = document.getElementById("week").value;
                    day = document.getElementById("day").value;
                }
                break;
            case "yearly":
                repeatInterval = document.getElementById("custom_recurrence_type_yearly").value;
                month = document.getElementById("month_of_year").value;
                if(isMonthView==true){
                    console.log(dateSet);
                    let dateStr = "";
                    dateSet.forEach(date=>{
                        dateStr += date;
                    });
                    date = dateStr;
                }
                else if (isWeekView==true){
                    week = document.getElementById("week").value;
                    day = document.getElementById("day").value;
                }
                break;
        }

        const package = {
            calendarId:calendar_id,
            schedule_id:schedule_id,
            title:title,
            description:description,
            allDay:all_day,
            startTime:start_time,
            endTime:end_time,
            isRecurring:repeat,
            frequency:frequency,
            repeatInterval:repeatInterval,
            day :day,
            date:date,
            month:month,
            week:week,
            offset:"1"
        }
        console.log("package: ",package);
        try{
            const response = await fetch(`http://localhost:8080/CalendarAPI/secured/schedule/put?user=${user}`,
                {
                    method:"POST",
                    headers:{
                        "Content-Type":"application/json",
                        "Authorization":`Bearer ${token}`,
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
                document.getElementById("custom_recurrence_type_weekly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_daily_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_monthly_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_yearly_div").style.display = 'none';
                document.getElementById("dropdown").style.display = 'none';
                document.getElementById("day_div").style.display = 'none';
                document.getElementById("custom_recurrence_type_div").style.display = 'none';

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

async function deleteEvent(schedule_id){
    try{
        const package = {
            "calendarId":calendar_id,
            "schedule_id":schedule_id
        };
        // const response = await fetch(`http://localhost:8080/CalendarAPI/event/delete?user=${user}&name=${title}`);
        const response = await fetch(`http://localhost:8080/CalendarAPI/secured/schedule/delete?user=${user}`,
            {
                method:"DELETE",
                headers:{
                    'Authorization':`Bearer ${token}`,
                },
                body:JSON.stringify(package),
            }
        );
        if(!response.status===401 || !response.ok){
            window.location.href="login.html";
        }
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

nextButton.addEventListener('click',()=> {
    currentYear++;
    render(currentYear);
    fetchCalendars();
    fetchEvents();
})

render(currentYear);