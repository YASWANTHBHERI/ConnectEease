console.log("contact script");

const baseURL = "http://localhost:8080"
const liveURL="https://connecteease.onrender.com";

const viewContactModal = document.getElementById("view_contact_modal");

const options = {
	placement: 'bottom-right',
	backdrop: 'dynamic',
	backdropClasses:
		'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
	closable: true,
	onHide: () => {

	},
	onShow: () => {

	},
	onToggle: () => {
		//console.log('modal has been toggled');
	},
};

const instanceOptions = {
	id: 'view_contact_modal',
	override: true
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
	contactModal.show();
}

function closeContactModal() {
	contactModal.hide();
}

async function loadContactData(id) {
	//console.log(id);

	try {
		const data = await (
			await fetch(`${liveURL}/api/contacts/${id}`)
		).json();
		//console.log(data);
		manipulateModalHtml(data, id);
	} catch (error) {

	}
}

function manipulateModalHtml(data, id) {
	document.querySelector("#contact_name").innerHTML = data.name;
	document.querySelector("#contact_email").innerHTML = data.email;
	document.querySelector("#contact_address").innerHTML = data.address;
	document.querySelector("#contact_body_email").innerHTML = data.email;
	document.querySelector("#contact_phone").innerHTML = data.phoneNumber;
	document.querySelector("#contact_profile_picture").src = (data.picture != null) ? data.picture : 'https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png';
	document.querySelector("#contact_description").innerHTML = data.description;
	document.querySelector("#contact_id").innerHTML = data.id;
	document.querySelector("#contact_website").setAttribute("href", data.websiteLink);
	document.querySelector("#contact_website").innerHTML = data.websiteLink;
	document.querySelector("#contact_linkedin").setAttribute("href", data.linkedInLink);
	document.querySelector("#contact_linkedin").innerHTML = data.linkedInLink;
	document.querySelector("#contact_favourite").innerHTML = data.favourite;

	// delete contact button in modal
	document.querySelector("#contact_delete_button").addEventListener("click", function() {
		deleteContact(id);
	})

	openContactModal();

}


// contacts delete
function deleteContact(id) {
	Swal.fire({
		title: "Do you want to Delete this Contact?",
		icon: "warning",
		showCancelButton: true,
		confirmButtonText: "Delete",
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
			const url = `${liveURL}/user/contacts/delete/${id}`;
			window.location.replace(url);
		}
	});
}


async function selectGroupOfContacts() {
	//console.log("checkboxes");
	const selectAllCheckbox = document.querySelector('#select_all_checkbox');
	selectAllCheckbox.addEventListener('change', function() {
		let checkboxes = document.querySelectorAll('.checkboxes');
		checkboxes.forEach(checkbox => {
			checkbox.checked = selectAllCheckbox.checked;

		});
	});

	const selectAllCheckboxStatus = selectAllCheckbox.checked;
	try {
		const url = `${liveURL}/user/contacts/send/verify/install?appCode=twilio`;
		//console.log(url);
		const response = await fetch(url);
		const jsonResponse = await response.json();
		//console.log(jsonResponse);
		if (jsonResponse.installed) {
			document.querySelector('#sendActionTwilioButton').style.display = selectAllCheckboxStatus ? 'inline-flex' : 'none';
		} else {
			document.querySelector('#marketPlaceButton').style.display = selectAllCheckboxStatus ? 'inline-flex' : 'none';
		}


	} catch (error) {
	}
	try {
		const url = `${liveURL}/user/contacts/send/verify/install?appCode=mailstrap`;
		//console.log(url);
		const response = await fetch(url);
		const jsonResponse = await response.json();
		//console.log(jsonResponse);
		if (jsonResponse.installed) {
			document.querySelector('#sendActionMailButton').style.display = selectAllCheckboxStatus ? 'inline-flex' : 'none';
		} else {
			document.querySelector('#marketPlaceButton').style.display = selectAllCheckboxStatus ? 'inline-flex' : 'none';
		}


	} catch (error) {
}}

async function sendActionMessage(appCode) {
	//console.log("send method called");
	let checkboxes = document.querySelectorAll('.checkboxes');
	let checkedValues = Array.from(checkboxes).filter(checkbox => checkbox.checked).map(checkbox => checkbox.value);
	//console.log(checkedValues);
	const queryString = checkedValues.map(val => `${encodeURIComponent(val)}`).join(',');
	//console.log("encode");
	//console.log(queryString);
	const url = `${liveURL}/user/contacts/send/${appCode}?id=` + checkedValues;
	//console.log(url);
	await fetch(url);

	// Redirect to the twilio page after sending the request
	window.location.href = `${liveURL}/user/contacts/send/${appCode}?id=${queryString}`;
}

function trackCheckboxes() {
	let checkboxes = document.querySelectorAll('.checkboxes');
	const sendMessageButton = document.querySelector('#sendActionTwilioButton');
	const sendMailButton = document.querySelector('#sendActionMailButton');

	function toggleButtonDisaply() {
		const isAnyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
		if (localStorage.getItem("twilioInstalled")) {
			//console.log("local storage if twilioInstalled")
			sendMessageButton.style.display = isAnyChecked ? 'inline-block' : 'none';
		}

		if (localStorage.getItem("mailstrapInstalled")) {
			//console.log("local storage if mailstrapInstalled")
			sendMailButton.style.display = isAnyChecked ? 'inline-block' : 'none';
		}

	}

	checkboxes.forEach(checkbox => {
		checkbox.addEventListener('change', toggleButtonDisaply);
	});
}
trackCheckboxes();