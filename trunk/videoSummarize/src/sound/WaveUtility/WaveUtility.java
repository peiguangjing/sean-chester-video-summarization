package sound.WaveUtility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.sound.sampled.*;


public class WaveUtility {

    private InputStream waveStream;
    private int bufferSeconds = 5; //buffer how many seconds of sound
    private int numBytes;
    private byte[] audioBytes;
    private int offset;
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private int bytesPerFrame;
    private float frameRate;
    
    public WaveUtility(InputStream waveStream, int bufferSeconds) {
    	this.waveStream = waveStream;
    	this.bufferSeconds = bufferSeconds;
    	
    	try {
    	    this.audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
    	} catch (UnsupportedAudioFileException e) {
    	    e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	this.audioFormat = this.audioInputStream.getFormat();
    	
    	this.bytesPerFrame = this.audioFormat.getFrameSize();
        if (this.bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
            System.err.println("no bytes per frame specified by the wav file");
        }
    	this.frameRate = audioFormat.getFrameRate();
    	if (this.frameRate == AudioSystem.NOT_SPECIFIED) {
            System.err.println("no frame rate specified by the wav file");
        }
    	
    	this.numBytes = (int) (Math.ceil(this.bytesPerFrame * this.frameRate * this.bufferSeconds));
    	this.audioBytes = new byte[this.numBytes];
    }

    public int readInBuffer () {
    	int offset = 0;
    	int numBytesRead = 0;
    	try {	
   		    while ((numBytesRead = this.audioInputStream.read(this.audioBytes, offset, this.numBytes - offset)) > 0) {
   		    	offset += numBytesRead;
   		    }
    	} catch (Exception ex) { 
    	    ex.printStackTrace();
    	}
    	
    	//audio bytes effective from 0 to offset - 1
    	this.offset = offset;
         	
    	if (numBytesRead == -1) {
    		return -1;
    	}
    	
    	return 0;
    }
    
    public double computeSoundLevel() {
    	double soundLevel = 0;
    	short shortAudio[] = new short[this.offset / 2];
    	ByteBuffer bb = ByteBuffer.wrap(this.audioBytes);
    	long sum = 0;
		for (int i = 0; i < shortAudio.length; i++) {
			shortAudio[i] = Short.reverseBytes(bb.getShort());
			//compute sound level model
			sum += Math.abs(shortAudio[i]);
		}
    	
		soundLevel = sum/shortAudio.length;
    	
    	return soundLevel;
    }
}
