package sound.WaveUtility;

import java.io.*;
import java.nio.ByteBuffer;

import javax.sound.sampled.*;


public class WaveUtility {

    private InputStream waveStream;
    private RandomAccessFile waveRAF;
    private double bufferSeconds = 5; //buffer how many seconds of sound
    private int numBytes;
    private byte[] audioBytes;
    private int offset;
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private int bytesPerFrame;
    private float frameRate;
    ByteArrayOutputStream baosAudio = new ByteArrayOutputStream();
    
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
    	
    	
    	createBuffer(bufferSeconds);
    }
    
    public WaveUtility(BufferedInputStream waveStream, RandomAccessFile waveRAF) {
    	this.waveStream = waveStream;
    	this.waveRAF = waveRAF;
    	
    	try {
    	    this.audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
    	} catch (UnsupportedAudioFileException e) {
    	    e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	this.audioFormat = this.audioInputStream.getFormat();
    	//dump to test
    	System.out.println("Wave Encoding: " + this.audioFormat.getEncoding().toString());
    	System.out.println("Wave Sample Rate: " + this.audioFormat.getSampleRate());
    	System.out.println("Wave Channels: " + this.audioFormat.getChannels());
    	
    	this.bytesPerFrame = this.audioFormat.getFrameSize();
        if (this.bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
            System.err.println("no bytes per frame specified by the wav file");
        }
    	this.frameRate = audioFormat.getFrameRate();
    	if (this.frameRate == AudioSystem.NOT_SPECIFIED) {
            System.err.println("no frame rate specified by the wav file");
        }
    	
    }
    
    public void createBuffer (double bufferSeconds) {
    	this.bufferSeconds = bufferSeconds;    	

    	this.numBytes = (int) (Math.ceil(this.bytesPerFrame * this.frameRate * this.bufferSeconds));
    	this.audioBytes = new byte[this.numBytes];
    }
    
    public byte[] getBuffer() {
    	return this.audioBytes;
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
    
    public int readInBufferRandom (long offet) {
    	int numBytes = 0;
    	int totalNumBytesRead = 0;
    	try {
    		this.waveRAF.seek(offet);
    		while (totalNumBytesRead < this.audioBytes.length && (numBytes = this.waveRAF.read(this.audioBytes)) >0 ) {
    			totalNumBytesRead += numBytes;
    		};
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	this.offset = totalNumBytesRead;
    	
    	if (numBytes == -1) {
    		return -1;
    	}
    	
    	return 0;
    }
    
    public void readInBufferRandomPeriod (double fromSecond, double toSecond) {
    	double duration = toSecond - fromSecond;
    	createBuffer(duration);
    	readInBufferRandom((long) (bytesPerFrame * frameRate * fromSecond));
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
    
    public double computeSoundLevelPeriod(double fromSecond, double toSecond) {
    	double soundLevel = 0;
    	long offset = 0;
    	
    	if (toSecond - fromSecond <= 0) {
    		return -1;
    	}
    	createBuffer((toSecond - fromSecond));
    	offset = (long) Math.floor(this.bytesPerFrame * this.frameRate * fromSecond);
    	readInBufferRandom(offset);
    	
    	soundLevel = this.computeSoundLevel();
    	
    	return soundLevel;
    }
    
    public void appendToOutputBuffer( byte[] bytes ) {
    	try {
    		baosAudio.write(bytes);
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
    
    public void saveWavFile (String filepath) {
   	  	
    	try {
    		OutputStream os = new FileOutputStream(filepath); 
    		DataOutputStream outFile = new DataOutputStream(os);
    		byte[] myData = baosAudio.toByteArray();
    		int myDataSize = myData.length;
    		//wave header
    		outFile.writeBytes("RIFF");
    		outFile.write(intToByteArray((int) (myDataSize + 44 - 8)), 0, 4);     // 04 - how big is the rest of the file
            outFile.writeBytes("WAVE");                
            outFile.writeBytes("fmt ");              
            outFile.write(intToByteArray((int) 16), 0, 4); // 16 - size of this chunk
            outFile.write(shortToByteArray((short) 1), 0, 2);        // 20 - what is the audio format? 1 for PCM = Pulse Code Modulation
            outFile.write(shortToByteArray((short) 1), 0, 2);  // 22 - mono or stereo?
            outFile.write(intToByteArray((int) audioFormat.getSampleRate()), 0, 4);        // 24 - samples per second (numbers per second)
            outFile.write(intToByteArray((int) (bytesPerFrame * frameRate)), 0, 4);      // 28 - bytes per second
            outFile.write(shortToByteArray((short) 2), 0, 2);    // 32 - # of bytes in one sample, for all channels
            outFile.write(shortToByteArray((short) 16), 0, 2); // 34 - how many bits in a sample(number)?  usually 16 or 24
            outFile.writeBytes("data");                 // 36 - data
            outFile.write(intToByteArray(myDataSize), 0, 4);      // 40 - how big is this data chunk, data size or +8?
            outFile.write(myData);                      // 44 - the actual data itself - just a long array of numbers
            
            outFile.close();
            os.close();
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
  	
    	
    }
    
    //little endian in wave
    public static byte[] shortToByteArray(short data) {
        return new byte[]{(byte) (data & 0xff), (byte) ((data >>> 8) & 0xff)};
    }
    
    public static byte[] intToByteArray(int i) {
    	byte[] b= new byte[4];
    	b[0] = (byte)(i & 0x000000FF);
    	b[1] = (byte)((i>>8) & 0x000000FF);
    	b[2] = (byte)((i>>16) & 0x000000FF);
    	b[3] = (byte)((i>>24) & 0x000000FF);
    	return b;
    }
    
}
