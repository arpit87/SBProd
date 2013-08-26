package in.co.hopin.HttpClient;

public abstract class SBHttpResponseListener {
	public boolean hasBeenCancelled = false;
	public abstract void onComplete(String response);
	public void onCancel()
	{
		hasBeenCancelled = true;
	}
}
