package publisher.rest.model.endpoint;

public enum EndpointStatus {

	VALID("VALID"), UNTESTED("UNTESTED"), INVALID("INVALID");

	private final String status;

	EndpointStatus(final String format) {
		this.status = format;
	}

	@Override
	public String toString() {
		return status.toLowerCase();
	}

	public static EndpointStatus fromString(String type) {
		if(type.equals("VALID"))
			return VALID;
		if(type.equals("UNTESTED"))
			return UNTESTED;
		if(type.equals("INVALID"))
			return INVALID;
		throw new IllegalArgumentException("Provided endpoint status is not valid");
	}
}
