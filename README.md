# NFCAttendance
CNIT 355 final project, using NFC to take help professors to take attendance in class.

## Install and Running

```
git clone https://github.com/thousight/NFCAttendace.git
```

Open project using Android Studio


## Features

- Android Studio
- NFC
- File IO

## Classes Description

- MainActivity:
	- Request permission on read/write external storage
	- Start student activity
	- Start professor activity
- StudentActivity:
	- Get device ID
	- Check NFC availability
	- Student inputs name
	- Create and send NFC record with student name and device ID inside
- ProfessorActivity:
	- Show the list of events in file directory
	- Set RecyclerView and connect it with RecyclerViewLayoutAdapter
	- FloatingActionButton to create new event
- RecyclerViewLayoutAdapter:
	- Stores the list of events
	- Item on click to view event details
	- Item long press to delete event
- NewEventActivity:
	- User enters event name through EditText field
	- User selects start and end time of the event through Calendar and Time widget in dialog
	- Create JSON object and store it in as a string in .txt file
- EventDisplayActivity:
	- Get details from file and display it
	- Show if user can check students in at current time
	- Use NFC to receive student information
	- Does not add student name into the list if current time is not in event time range and if student already checked in with the same device
	- Show list of students
	- Back button to go back to ProfessorAcitivity