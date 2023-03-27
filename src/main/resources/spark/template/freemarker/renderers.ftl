<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="author" content="Andrea Cimmino">

    <title>KG Builder</title>

    <#include "./fragments/css-imports.ftl"/>
  </head>

  <body>
    
  <#include "./fragments/navbar.html"/>

  <div class="container-fluid">
    <div class="row">

      <#include "./fragments/sidebar.html"/>
      

      <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
        <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
          <h1 class="h2">HTML Renderer Engines</h1>
          <div class="btn-toolbar mb-2 mb-md-0">
            <div class="btn-group me-2">
            </div>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-striped align-middle" id="dtBasicExample">
            <thead>
               <tr style="text-align:center;">
                  <th class="th-sm" style="text-align:center;">Renderer</th>
                  <th class="th-sm" style="text-align:center;">Templates directory</th>
                  <th class="th-sm" style="text-align:center;">#</th>
               </tr>
            </thead>
            <tbody id="tableBody">
              <#list renderers as renderer>
                <tr style="text-align: center;">
                  <td>${renderer.@type}</td>
                  <td>${renderer.templatesDir }</td>
                  <td><button type="button" class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#editModal" onclick="editObject('${renderer.id}')" style="margin-right:5px"> <i class="bi bi-pencil-fill"></i></button></td>
                </tr>
              <#else>
                <tr style="text-align: center;">
                  <td>No data :(</td>
                  <td></td>
                  <td></td>
                </tr>
              </#list>
            </tbody>
          </table>
        </div>
        <div class="row">
          <h1 class="h3">Static files directory</h1>
          <div class="col-md-12"> 
            <label for="staticFilesDirectory">Local directory</label>
            <input type="text" class="form-control" id="staticFilesDirectory" value="${config.staticFilesDirectory}" >
            <button type="button" class="btn btn-primary" style="margin-top:10px" onclick="persistStaticDirectory()">Save</button>
          </div>
        </div>

        <#include "./fragments/toasts.html"/>
        
        

      </main>
    </div>
  </div>

  <#include "./fragments/js-imports.ftl"/>
   

    <!-- MODALS -->
    <!-- Add Modal -->
    <div class="modal fade"  id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h1 class="modal-title fs-5" id="editModalLabel">Renderer data</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <!-- Modal Body-->

            <form id="inputForm">
              <div class="form-group">
                <input type="text" class="form-control" id="id" style="display: none;">
                <label for="route">Renderer name</label>
                <input type="text" class="form-control" id="@type" disabled>
              </div>
              <div class="form-group">
                <label for="templatesDir">Templates directory</label>
                <input type="text" class="form-control" id="templatesDir">
              </div>
              
              
            </form>
            
            <!-- /Modal body-->
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" id="closeModal">Close</button>
            <button type="button" class="btn btn-primary" onclick="updateRow()">Save</button>
          </div>
        </div>
      </div>
    </div>
    <!-- /Add modal-->

        
  </body>

</html>
<script>
  var base_domain = "/api/renderers";

  $(document).ready(function(){
    $("#menu_renderers").addClass('active'); // setup active link
  });

  function buildSystemConfigJson(){
    let output = {};
    var templatesDir = $("#staticFilesDirectory").val();
    output["staticFilesDirectory"] = templatesDir;
    return JSON.stringify(output);
  }

  function persistStaticDirectory(){
    var json = buildSystemConfigJson();
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
    console.log("CREATE/UPDATE operation request: /api/system");
    console.log(json);   
    xmlHttp.open("POST", "/api/system", true); // true for asynchronous 
    xmlHttp.send(json);
  }

  
</script>

