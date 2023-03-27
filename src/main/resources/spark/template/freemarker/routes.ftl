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
          <h1 class="h2">Routes published</h1>
          <div class="btn-toolbar mb-2 mb-md-0">
            <div class="btn-group me-2">
              <button type="button" class="btn btn-sm btn-outline-success" id="addNewEndpoint" data-bs-toggle="modal" data-bs-target="#editModal"><i class="bi bi-plus-circle"></i> Add</button>
             
            </div>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-striped align-middle" id="dtBasicExample">
            <thead>
               <tr style="text-align:center;">
                  <th class="th-sm" style="text-align:center;">Route</th>
                  <th class="th-sm" style="text-align:center;">Regex Address</th>
                  <th class="th-sm" style="text-align:center;">HTML views</th>
                  <th class="th-sm" style="text-align:center;">Endpoint</th>
                  <th class="th-sm" style="text-align:center;">#</th>
               </tr>
            </thead>
            <tbody id="tableBody">
              <#list routes as route>
                <tr style="text-align: center;">
                  <td>${route.route}</td>
                  <td>${route.isRegex?string('yes', 'no') }</td>
                  <td><#if route.view??>(${route.view.render.@type}) ${route.view.template}<#else>-</#if> </td>
                  <td><#if route.endpoint??>${route.endpoint.name}<#else>-</#if> </td>
                  <td>
                    <!--NOT SURE IF WE SHOULD KEEP THIS <button type="button" class="btn btn-sm btn-info" onclick="testRoute('${route.route}')" style="margin-right:5px"> <i class="bi bi-play"></i></button>-->
                    <button type="button" class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#editModal" onclick="editObject('${route.id}')" style="margin-right:5px"> <i class="bi bi-pencil-fill"></i></button>
                    <button onclick="deleteRow('${route.id}')" type="button" class="btn btn-sm btn-danger"><i class="bi bi-trash3-fill"></i></button></td>

                  </td>
                </tr>
              <#else>
                <tr style="text-align: center;">
                  <td>No data :(</td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                  <td></td>
                </tr>
              </#list>
            </tbody>
          </table>
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
            <h1 class="modal-title fs-5" id="editModalLabel">Route's data</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <!-- Modal Body-->

            <form id="inputForm">
              <div class="form-group">
                <input type="text" class="form-control" id="@type" style="display: none;" value="PublisherRoute">
                <input type="text" class="form-control" id="id" style="display: none;">
              </div>
              <div class="form-group">
                <label for="route">Route address</label>
                <input type="text" class="form-control" id="route" placeholder="Enter a route address to publish data">
              </div>
              <div class="form-group">
                <label for="isRegex">Route is regex?</label>
                  <select class="form-select" id="isRegex">
                    <option value="false">No</option>
                    <option value="true">Yes</option>
                  </select>
              </div>
              <div class="form-group">
                <label for="route">HTML template</label>
                <#if views??>
                <select class="form-select" id="view">
                    <option value="None">None</option>
                    <#list views as view>
                      <option value="${view.id}">(${view.render.@type}) ${view.template}</option>
                    </#list>
                </select>
                <#else>
                  <select class="form-select" id="view" disabled> </select>
               </#if>
              </div>
              <div class="form-group">
                <label for="endpoint">Data source:</label>
                <select class="form-select" id="endpoint">
                   <option value="None">None</option>
                      <#list aggregated as endpoint>
                         <option value="${endpoint.id}">${endpoint.name}</option>
                      <#else><option disabled>No aggregated</option> </#list>
                  
                      <#list endpoints as endpoint>
                         <option value="${endpoint.id}">${endpoint.name}</option>
                      <#else><option disabled>No endpoint</option>  </#list>
                   
                </select>
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
  var base_domain = "/api/routes";

  function appendRow(json){

  }
  function testRoute(route){
    window.location.href = "/api/test?route="+route;
  }

  $(document).ready(function(){
    $("#menu_routes").addClass('active'); // setup active link
  });
  // only for smart tables
  $(document).ready(function () {
      $('#dtBasicExample').DataTable({
          "order": [[0, 'desc']],
          "pagingType": "simple",
          "searching": true
      });
      $('.dataTables_length').addClass('bs-select');
  });
  // only for select with search bar
  // TODO: REMOVE, ALSO FROM JS IMPORTS var dselect = dselect(document.querySelector('#endpoint'));
</script>

