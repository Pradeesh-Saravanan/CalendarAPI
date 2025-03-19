const url = "http://localhost:8080/CalendarAPI/";

const currentYearElement = document.getElementById("current-year");
const calendarMonths = document.getElementById("calendar-months");
const prevButton = document.getElementById("prev-year");
const nextButton = document.getElementById("next-year");

let currentYear = new Date().getFullYear();


function doGet(){
    
    fetch("http://localhost:8080/CalendarAPI/calendar/")
    
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