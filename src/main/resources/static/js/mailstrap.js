//console.log("mailstrap script loaded");

const baseUrl = "http://localhost:8080"
const liveUrl="https://connecteease-production.up.railway.app";

function removeRecipient(item) {
	const recipientItem = item.parentNode;
	recipientItem.remove();
}


async function sendMessage() {
	//clearing errors on submit
	clearErrors();

	//getting sender
	const fromRecipient = document.querySelector("#from_email").value;
	const senderError = document.querySelector("#sender-error");



	//getting recipients

	const recipientsList = document.querySelector("#recipientslist");
	const recipients = Array.from(recipientsList.children).map(item => `${item.querySelector("span").textContent}`);
	//console.log(recipients);
	const recipientsError = document.querySelector("#recipients-error");

	//getting subject
	const subject = document.querySelector('#subject').value;
	const subjectError = document.querySelector('#subject-error');
	

	//getting message body
	const messageBody = document.querySelector("#message").value;
	//console.log(messageBody);
	const messageError = document.querySelector("#message-error");



	if (fromRecipient == '') {
		senderError.textContent = "Please select from address.";
		//console.log("no sender");
		return;
	}

	if (recipients.length < 1) {
		recipientsError.textContent = "Please add at least one recipient.";
		//console.log(recipients.length);
		return;
	}
	if (subject == '') {
			subjectError.textContent = "Subject cannot be empty.";
			//console.log("Subject is empty");
			return;
		}

	if (messageBody == '') {
		messageError.textContent = "Message cannot be empty.";
		//console.log("message body empty");
		return;
	}


	//console.log("data conversion");
	const data = {
		fromEmail:fromRecipient,
		recipients: recipients,
		subject:subject,
		body: messageBody
	}
	try {
		//console.log("api called");
		const response = await fetch(`${liveUrl}/user/contacts/send/mailstrap/mail`, {
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

	}

}


// validations

function clearErrors(element) {
	document.querySelector("#sender-error").textContent = "";
	document.querySelector("#recipients-error").textContent = "";
	document.querySelector("#subject-error").textContent = "";
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