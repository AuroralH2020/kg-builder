<h2>Triplets</h2>

<#list data?split('\n') as row>
	<p> ${row}</p>
<#else>
	No triplets :(
</#list>