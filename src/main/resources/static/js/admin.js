console.log("adminjs script loaded");


document.querySelector("#contact_image_input").addEventListener("change", function(event) {
	let file = event.target.files[0];
	let reader = new FileReader();
	reader.onload = function() {
		document.querySelector("#upload_image_preview").setAttribute("src", reader.result);
	};
	reader.readAsDataURL(file);
});


// country code
const input = document.querySelector("#phonenumber");
const iti = window.intlTelInput(input, {
	initialCountry: "in",
	separateDialCode: true,
	utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js" // only needed for placeholder functionality
});

document.querySelector('.iti').classList.add('w-full');

const phoneInput = document.querySelector('#phonenumber');
const countryCodeInput = document.querySelector("#country_code");

phoneInput.addEventListener("countrychange", function() {
	const countryData = iti.getSelectedCountryData();
	countryCodeInput.value = countryData.dialCode; // Update hidden input with country code
	//console.log(countryData);
});


document.querySelector("form").addEventListener("submit", function() {
	const fullNumber = iti.getNumber();  // Get full phone number in international format
	phoneInput.value = fullNumber; // Set it to the phone input for form submission
	//console.log(phoneInput.value);
});