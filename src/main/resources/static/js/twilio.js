//console.log("twilio script loaded")
const baseUrl = "http://localhost:8080"
const liveURL = "";

function removeRecipient(item) {
	const recipientItem = item.parentNode;
	recipientItem.remove();
}


async function sendMessage() {
	//clearing errors on submit
	clearErrors();

	//getting sender
	const selectedSenderList = document.querySelector("#senderlist");
	const selectedSenderOption = selectedSenderList.value;
	//console.log(selectedSenderOption);
	const senderError = document.querySelector("#sender-error");



	//getting recipients

	const recipientsList = document.querySelector("#recipientslist");
	const recipients = Array.from(recipientsList.children).map(item => `${item.querySelector("span").textContent}`);
	//console.log(recipients);
	const recipientsError = document.querySelector("#recipients-error");


	//getting message body
	const messageBody = document.querySelector("#message").value;
	//console.log(messageBody);
	const messageError = document.querySelector("#message-error");



	if (selectedSenderOption == '') {
		senderError.textContent = "Please select a sender.";
		//console.log("no sender");
		return;
	}

	if (recipients.length < 1) {
		recipientsError.textContent = "Please add at least one recipient.";
		//console.log(recipients.length);
		return;
	}

	if (messageBody == '') {
		messageError.textContent = "Message cannot be empty.";
		//console.log("message body empty");
		return;
	}


	//console.log("data conversion");
	const data = {
		sender: selectedSenderOption,
		recipientsList: recipients,
		message: messageBody
	}
	try {
		const response = await fetch(`${baseUrl}/user/contacts/send/twilio/message`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(data)
		});
		const jsonResponse = await response.json();
		//console.log("sending status",jsonResponse);

		showToast();
		resetMessageConfirmation('message');

	} catch (error) {
		//console.log(error);
	}

}


// validations

function clearErrors(element) {
	document.querySelector("#sender-error").textContent = "";
	document.querySelector("#recipients-error").textContent = "";
	document.querySelector("#message-error").textContent = "";
}


// sent message status

// Select the toast element and the close button
const toastEl = document.getElementById('toast-messagesent');
const closeButton = document.getElementById('toast-close-button');

// Function to show the toast
function showToast() {
	// Remove the 'hidden' class to make the toast visible
	toastEl.classList.remove('hidden');

	// Automatically hide the toast after 3 seconds
	setTimeout(() => {
		hideToast();
	}, 3000);
}

// Function to hide the toast
function hideToast() {
	// Add the 'hidden' class to hide the toast
	toastEl.classList.add('hidden');
}

// Event listener for the close button to manually dismiss the toast
closeButton.addEventListener('click', hideToast);


// Reset Message
function resetMessage(elementId){
	//console.log(document.querySelector("#message").value);
	document.querySelector(`#${elementId}`).value="";
}

//reset message confirmation
// contacts delete
function resetMessageConfirmation(id) {
	Swal.fire({
		title: "Do you want to Reset the Message",
		icon: "question",
		showCancelButton: true,
		confirmButtonText: "Reset",
		buttonsStyling: false,
		customClass: {
			title: "text-lg",
			confirmButton: "bg-red-500 hover:bg-black text-white font-bold py-2 px-4 rounded mr-3", // Custom or Tailwind classes
			cancelButton: "bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded",
			ok: "text-black"
		}
	}).then((result) => {
		/* Read more about isConfirmed, isDenied below */
		if (result.isConfirmed) {
			resetMessage(id);
		}
	});
}



//excel reader
function importExcelFile() {
	const input = document.createElement('input');
	input.setAttribute('type', 'file');
	input.setAttribute('accept', '.xlsx');
	input.click();

	input.addEventListener('change', (event) => {
		const file = event.target.files[0];
		let phoneColIndex = -1;
		const phoneNumbersList = [];

		readXlsxFile(file).then((rows) => {
			//console.log(rows);
			rows.forEach((row, rowIndex) => {

				row.forEach((cell, cellIndex) => {
					//console.log(typeof(cell),cell);
					if (rowIndex == 0 && typeof (cell) == 'string') {
						if (cell.toLowerCase().includes('phone') || cell.toLowerCase().includes('mobile')) {
							//console.log(cell, cellIndex);
							phoneColIndex = cellIndex;
						}
					}

					if (cellIndex == phoneColIndex && rowIndex > 0) {
						//console.log(cell);
						phoneNumbersList.push(cell);
					}
				});

			});

			//console.log(phoneNumbersList);
			//checking the length of phoneNumbersList
			if (phoneNumbersList.length < 1) {
				return;
			}
			const recipientslistElement = document.querySelector('#recipientslist');
			phoneNumbersList.forEach((number) => {
				const liElement = document.createElement('li');
				liElement.setAttribute('class', 'bg-white text-green-500 pl-1 pr-1 border border-gray-600 rounded-xl cursor-pointer');

				const spanElement = document.createElement('span');
				spanElement.innerHTML = number;
				spanElement.style.textDecoration = 'none'; 

				const closeIcon = document.createElement('i');
				closeIcon.setAttribute('class', 'fa-solid fa-circle-xmark');
				closeIcon.style.textDecoration = 'none'; 
				closeIcon.addEventListener('click', function(e) {
					removeRecipient(spanElement);
				});
				

				spanElement.appendChild(closeIcon);
				liElement.appendChild(spanElement);
				recipientslistElement.appendChild(liElement);
			});

		});


	});
	

}
