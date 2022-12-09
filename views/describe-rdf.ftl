<#assign dataProperties = 0>
<#assign typeProperties = 0>
<#assign sameAsProperties = 0>
<#assign objectProperties = 0>
<#assign triplets = []>

<#list data?split("\\s*\\.\\n","r") as row>
	<#assign cleanRow = row?trim>
	<#assign subject = cleanRow?keep_before(" ")?trim>
	<#assign postAmble = cleanRow?keep_after(" ")>
	<#assign predicate = postAmble?keep_before(" ")?keep_after("<")?keep_before(">")?trim>
	<#assign object = postAmble?keep_after(" ")?trim>
	<#assign literal = "">
	<#assign type = "dp">
	<#if subject?starts_with("<")>
		<#assign subject = subject?keep_after("<")?keep_before(">")?trim>
	</#if>
	<#if object?starts_with("<") >
		<#assign objectProperties = objectProperties+1>
		<#assign object = object?keep_after("<")?keep_before(">")?trim>
		<#assign type = "op">
	<#elseif object?starts_with("_:")>
	 	<#assign objectProperties = objectProperties+1>
	 	<#assign type = "op">
	<#else>
		<#assign dataProperties = dataProperties+1>
		<#if object?matches("[^@]+@[a-z][a-z]$","r")>
			<#assign literal = "@" + object?keep_after_last("@")>
			<#assign object = object?keep_before_last("@")?trim>
		<#elseif object?contains("^^<") && object?ends_with(">")>
			<#assign literal = object?keep_after_last("<")?keep_before(">")>
			<#if literal?starts_with("http://www.w3.org/2001/XMLSchema#")>
				<#assign literal = literal?replace("http://www.w3.org/2001/XMLSchema#", "xsd:")>
			</#if>
			<#assign object = object?keep_before("^")?trim>
		</#if>
	</#if>

	<#assign triplets = triplets + [[subject, predicate, object, literal, type]]>
</#list>