console.log("hi!");
var ignoreObjectKeys = [];
function formatErrorText(xmlHttp){
  var p = xmlHttp.responseText;
  //return p.replace(/^.*Exception:\s*/i, '').replace(/"}\s*$/i,'');     
  return p.replace(/^.*publisher.rest.exception.InvalidRequestException:/i, '').replace(/"}\s*$/i,'');           
}

function closeToast(){
  $('#toastBackendError').toast('hide');
}



function deleteRow(id){
  var xmlHttp = new XMLHttpRequest();
  xmlHttp.onreadystatechange = function() { 
    if (xmlHttp.readyState == 4 && xmlHttp.status >= 200 && xmlHttp.status < 300){
      location.reload();
    }else if(xmlHttp.readyState == 4 && xmlHttp.status == 404){
      $('#toastMessage').text("Requested route does not exist in the backend!");
      $('#toastBackendError').toast('show');
    }else if(xmlHttp.readyState == 4){
      var message = formatErrorText(xmlHttp);
      console.log("DELETE operation response: "+message+" ("+xmlHttp.status+")");
      $('#toastMessage').text(message);
      $('#toastBackendError').toast('show');
    }
       
    }; 
  console.log("DELETE operation request: "+base_domain+"/"+id);
  xmlHttp.open("DELETE", base_domain+"/"+id, true); // true for asynchronous 
  xmlHttp.send(null);
}

function clearForm() {
  var inputs = $('#inputForm').find("div.form-group > input, select, textarea");
  for (let i = 0; i < inputs.length; i++) {
    var input = inputs[i];
    var value = input.value;
    var key = input.id;
    console.log("Clearing form: "+key+" -> "+value);
    if(key && value)
      document.getElementById(key).value='';
  }
  var uls = $('#inputForm').find("div.form-group > ul");
  for (let i = 0; i < inputs.length; i++) {
    var ul = uls[i];
    if(ul){
      $('#'+ul.id).empty();
    }
  }
}

  // Always clear add form when clicking add new
  document.getElementById("addNewEndpoint").addEventListener("click", clearForm);


function formToJSON() {
  let output = {};

  var inputs = $('#inputForm').find("div.form-group > input,select,textarea");
  for (let i = 0; i < inputs.length; i++) {
    var input = inputs[i];
    var key = input.id;
    var value = input.value;
    console.log("Creating json: "+key+" -> "+value);
    if(input.type=="checkbox"){
      output[key] = document.getElementById(input.id).checked;
    }else{
      if(key && value && !ignoreObjectKeys.includes(key))
        output[key] = value;
    }
    
   
  }
  var uls = $('#inputForm').find("div.form-group > ul");
  for (let i = 0; i < inputs.length; i++) {
    var ul = uls[i];
    if(ul){
      console.log(ul);
      var ulId = ul.id;
      var lis = $('#'+ulId).children();
      var lisIds = []
      for (let j = 0; j < lis.length; j++) {
        console.log(lis[j])
        var id = lis[j].id;
        lisIds.push(id);
      }
      if(ulId && lisIds)
        output[ulId] = lisIds
    }
  }
  return JSON.stringify( output );
}

function updateRow(){
  var json = formToJSON();
  var xmlHttp = new XMLHttpRequest();
  xmlHttp.onreadystatechange = function() { 
    if (xmlHttp.readyState == 4 && xmlHttp.status >= 200 && xmlHttp.status < 300){
      location.reload();
    }else if(xmlHttp.readyState == 4){
      $('#editModal').modal('hide');
      var message = formatErrorText(xmlHttp);
      console.log("CREATE/UPDATE operation response: "+message+" ("+xmlHttp.status+")");
      $('#toastMessage').text(message);
      $('#toastBackendError').toast('show');
    }     
  } 
  console.log("CREATE/UPDATE operation request: "+base_domain);
  console.log(json);   
  xmlHttp.open("POST", base_domain, true); // true for asynchronous 
  xmlHttp.send(json);

}
function editObject(id){
  var xmlHttp = new XMLHttpRequest();
  xmlHttp.onreadystatechange = function() { 
    if (xmlHttp.readyState == 4 && xmlHttp.status >= 200 && xmlHttp.status < 300){
      clearForm();
      var json = JSON.parse(xmlHttp.responseText);
      const keys = Object.keys(json);
       for (let i = 0; i < keys.length; i++) {
        var key = keys[i];
        var value = json[key];
        var valueType = Object.prototype.toString.call(value);
          console.log(key+" -> "+value+" :"+valueType );
          if(valueType == '[object Object]'){
              document.getElementById(key).value = value.id;
              console.log("\t[EDIT] "+key+" -> "+ value.id);
          }else if(valueType == '[object Array]'){
              console.log("editObject function does not work for arrays, copy and override the function")
          }else if(valueType == '[object String]'){
            console.log(key+" : "+value);
            document.getElementById(key).value = value;
          }else if(valueType == '[object Boolean]'){
            console.log(key+" : "+value+" -> "+ (value==true));
            document.getElementById(key).checked = value;
          } else{
            console.log("eror, unknown data type!");
          }
      }
      
    } else if (xmlHttp.readyState == 4 ){
      var message = formatErrorText(xmlHttp);
      console.log("READ operation response: "+message+" ("+xmlHttp.status+")");
      $('#toastMessage').text(message);
      $('#toastBackendError').toast('show');
    }
  }; 
  console.log("READ operation request: "+base_domain+"/"+id);
  if(Boolean(id)){
    xmlHttp.open("GET", base_domain+"/"+id, true); // true for asynchronous 
    xmlHttp.send(null);
  }else{
    $('#toastMessage').text("Selected element has no id, refreshing the page will solve this issue");
    $('#toastBackendError').toast('show');
  }

}