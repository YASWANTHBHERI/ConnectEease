//console.log("marketplace loaded..")
const baseUrl = "http://localhost:8080";
const liveURL ="";

const twilio_intstall_button = document.querySelector("#twilio_install_button")

const marketPlaceInstallModal = document.getElementById("marketplace_install_modal");

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

	},
};

const instanceOptions = {
	id: 'marketplace_install_modal',
	override: true
};

const marketPlaceModal = new Modal(marketPlaceInstallModal, options, instanceOptions);

function openMarketPlaceModal(app_name) {
	document.querySelector(".marketPlaceModal_app_install_button").setAttribute("id",app_name);
	let installedAppName=app_name[0].toUpperCase()+app_name.slice(1);
	document.querySelector("#marketplace_app_name").innerHTML = installedAppName;
	document.querySelector("#marketplace_product").innerHTML = app_name.includes('twilio')?'messaging':'mail';
	marketPlaceModal.show();
	
}

function closeMarketPlaceModal() {
	marketPlaceModal.hide();
}



function openExtensionInstallationModal(app_name){
	openMarketPlaceModal(app_name);
}

async function installMarketPlaceApp(){
	const appcode=document.querySelector(".marketPlaceModal_app_install_button").getAttribute("id");
	const url = `${baseUrl}/user/contacts/marketplace/install?appcode=`+appcode;
	
	try{
		//console.log(url);
	const response = await fetch(url);
	//console.log("marketplace installation response",response);
	localStorage.setItem(appcode+"Installed",true);
	//console.log("replacing user to login");
		window.location.replace(`${baseUrl}/user/contacts/send/${appcode}?id=123`);
	}catch(error){

	}
	
}
