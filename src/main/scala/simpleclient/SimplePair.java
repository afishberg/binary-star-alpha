package simpleclient;

import java.net.*;
import java.io.*;

public class SimplePair implements Serializable {
	public String key;
	public int value;

	public SimplePair(String k, int v) {
		this.key = k;
		this.value = v;
	}
}
