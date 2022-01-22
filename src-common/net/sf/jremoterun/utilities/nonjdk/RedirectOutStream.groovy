package net.sf.jremoterun.utilities.nonjdk

import net.sf.jremoterun.utilities.FileOutputStream2
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.output.TeeOutputStream;

public class RedirectOutStream {

	private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());
	
	public static FileOutputStream2 setOutStreamWithRotation(File file,int maxCount) throws Exception {
		if(file==null){
			throw new NullPointerException("argument file is null")
		}
		if (file.exists()) {
			try {
				FileRotate.rotateFile(file, maxCount);
			} catch (Exception e) {
				log.log(Level.SEVERE, "failed rotate file : "+file,e);
			}
		}
		return setOutStreamNoRotation(file);
	}

	@Deprecated
	public static FileOutputStream2 setOutStreamNoRotation(File file) throws Exception {
		return ConsoleRedirect.setOutputToConsoleAndFile3 (file);
	}


	public static void addOutStream(OutputStream outToAdd) throws Exception {
		if(SetConsoleOut2.proxyErr==null){
			SetConsoleOut2.setConsoleOut()
		}
		TeeOutputStream outputStream1 = new TeeOutputStream(SetConsoleOut2.proxyOut.getNestedOut(), outToAdd);
		TeeOutputStream outputStream2 = new TeeOutputStream(SetConsoleOut2.proxyErr.getNestedOut(), outToAdd);
		SetConsoleOut2.proxyOut.setNestedOut outputStream1
		SetConsoleOut2.proxyErr.setNestedOut outputStream2
	}
}
