import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;


//http://commons.apache.org/proper/commons-codec
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.codec.binary.Base64;


import java.io.File;
import java.io.FileOutputStream;
import java.io.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;



/*
 * This sample code was written by Aaron M. Renn and is a demonstration
 * of how to utilize some of the features of the GNU getopt package.  This
 * sample code is hereby placed into the public domain by the author and
 * may be used without restriction.
 */

public class CryptMain
{
	private static String CONFIG_FILE_EXTENSION = ".cfg";
	private static String ENCRYPTED_CONFIG_FILE_EXTENSION = ".cfx";
	private static String usage = 
		"Usage: crypt [OPTION]... [-s] string \n" +
		"   or: crypt [OPTION]... [-d] path \n" +
		"   or: crypt [OPTION]... [-e] path \n" +
		"\n" +
		"Mandatory arguments to long options are mandatory for short options too. \n" +
		"  -s, --string 	 given string to encrypt, max length is 128 \n" +
		"  -m, --method      string encrypt method, 0=MD5(default), 1=DES3+BASE64 \n" +
		"  -d, --decrypt     decrypt the given encrypted config file to plain *.cfg file \n" +
		"  -e, --encrypt     encrypt the given plain config file to encrypted *.cfx file \n\n";

	private static String version = "1.0.0";

	public static void main(String[] argv)
	{
		int c = 0;
		String arg = "";
		StringBuffer plainFileName = new StringBuffer();
		StringBuffer cryptFileName = new StringBuffer();

		LongOpt[] longopts = new LongOpt[6];
		longopts[0] = new LongOpt( "string",    LongOpt.REQUIRED_ARGUMENT, null, 's' );
		longopts[1] = new LongOpt( "decrypt",   LongOpt.REQUIRED_ARGUMENT, cryptFileName, 'd' );
		longopts[2] = new LongOpt( "encrypt",   LongOpt.REQUIRED_ARGUMENT, plainFileName, 'e' );
		longopts[3] = new LongOpt( "method",    LongOpt.OPTIONAL_ARGUMENT, null, 'm' );
		longopts[4] = new LongOpt( "version",   LongOpt.NO_ARGUMENT,		null, 'v' );
		longopts[5] = new LongOpt( "help",      LongOpt.NO_ARGUMENT,		null, 'h' );

		Getopt g = new Getopt("CryptMain", argv, "vhs:m::d:e:", longopts);
		g.setOpterr(false); // We'll do our own error handling

		int work = 0;
		int method = 0;
		String plainText = "";
		String path = "";

		int v =0, h = 0;
		while ((c = g.getopt()) != -1)
		switch (c)
		{
			case 'h':
				System.out.println(usage);
				h = 1;
			break;

			case 'v':
				System.out.println(version);
				v = 1;
			break;

			case 's':
				arg = g.getOptarg();
				if (null==arg)
				{
					System.out.println("option '" + (char)c + "' with NULL argument! ");
					break;
				}
				if (128 < arg.length())
				{
					System.out.println("option '" + (char)c + "' with argument " + arg + " too long!");
					break;
				}
				if (0 == work)
				{
					//System.out.println("You picked option '" + (char)c + "' with argument " + arg);
					work = 1;
					plainText = arg;
				}
				else
				{
					System.out.println("Ignore picked option '" + (char)c + "' with argument " + arg);
				}
			break;

			case 'd':
				arg = g.getOptarg();
				if (null==arg)
				{
					System.out.println("option '" + (char)c + "' with NULL argument! ");
					break;
				}
				if (0 == work)
				{
					//System.out.println("You picked option '" + (char)c + "' with argument " + arg);
					work = 2;
					path = arg;
				}
				else
				{
					System.out.println("Ignore picked option '" + (char)c + "' with argument " + arg);
				}
			break;

			case 'e':
				arg = g.getOptarg();
				if (null==arg)
				{
					System.out.println("option '" + (char)c + "' with NULL argument! ");
					break;
				}
				if (0 == work)
				{
					//System.out.println("You picked option '" + (char)c + "' with argument " + arg);
					work = 3;
					path = arg;
				}
				else
				{
					System.out.println("Ignore picked option '" + (char)c + "' with argument " + arg);
				}
			break;

			case 'm':
				arg = g.getOptarg();
				if (null==arg)
				{
					System.out.println("option '" + (char)c + "' with NULL argument! But Ignore it anyway!\n");
					break;
				}
				
				try
				{
					method = Integer.parseInt(arg);
					if (1==work)
					{
						//System.out.println("You picked option '" + (char)c + "' with argument " + method);
					}
					else
					{
						System.out.println("Ignore picked option '" + (char)c + "' with argument " + method);
					}
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}

			break;

			case ':':
				System.out.println("Error: You need an argument for option " + (char)g.getOptopt());
			break;

			case '?':
				char opt = (char)g.getOptopt();
				if (('s' == opt) ||
					('d' == opt) ||
					('e' == opt))
				{
					System.out.println("No argument specified with option '" + opt + "'");
					break;
				}
				else
				{
					System.out.println("The option '" + opt + "' is not valid!");
				}

				System.out.println(usage);
			break;

			default:
				System.out.println("Error: unknown argument! " + c);
			break;
		}

		for (int i = g.getOptind(); i < argv.length ; i++)
		{
			System.out.println("Ignore Non option argv element: " + argv[i] + "\n");
			return;
		}

		int optind = g.getOptind();
		int argc = argv.length;
		//System.out.println("work=" + work +", optind="+ optind + ", argc=" + argc);
		if (0 == work || optind < argc || (1 == work && argc < 3) || (1<work && argc < 2))
		{
			if (0==v && 0==h)
			{
				System.out.println(usage);
			}
			return;
		}

		String fileExtensionName = "";
		String fileNameNoExtension = "";
		String out = "";

		switch (work)
		{
			case 1:
			{
				if (0 == method)
				{
					try
					{
						final byte[] keyBytes = plainText.getBytes("utf-8");
						// given a salt with prefix '$1$'
						final String salt = "$1$";
						String cipher = Md5Crypt.md5Crypt(keyBytes, salt);
						
						System.out.println("MD5 Encrypt string=" + plainText + " To " + cipher);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else if (1 == method)
				{
					try
					{
						final byte[] bytes = TripleDES.encrypt(plainText);
						String cipher = new String(Base64.encodeBase64(bytes), "utf-8");
						cipher = "{\"" + cipher + "\"}";
						
						System.out.println("DES3+BASE64 Encrypt string=" + plainText + " To " + cipher);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("Error: Invalid Encrypt method " + method);
				}
			}
			break;

			case 2:
				fileExtensionName = getExtensionName(path);
				fileNameNoExtension = getFileNameNoEx(path);
				//System.out.println("fileExtensionName=" +fileExtensionName + ", fileNameNoExtension=" + fileNameNoExtension);

				if (fileExtensionName.equals(CONFIG_FILE_EXTENSION))
				{
					System.out.println( "ignore plain file=" + path);
				}
				else if (fileExtensionName.equals(ENCRYPTED_CONFIG_FILE_EXTENSION))
				{
					out = fileNameNoExtension + CONFIG_FILE_EXTENSION;
					System.out.println( "Decrypt file=" + path + " To " + out + ((decryptConfigFile(path, out))?" successfully":" failed"));
				}
				else
				{
					System.out.println( "Error: extension of file must be " + ENCRYPTED_CONFIG_FILE_EXTENSION);
				}
			break;

			case 3:
				fileExtensionName = getExtensionName(path);
				fileNameNoExtension = getFileNameNoEx(path);
				//System.out.println("fileExtensionName=" +fileExtensionName + ", fileNameNoExtension=" + fileNameNoExtension);

				if (fileExtensionName.equals(CONFIG_FILE_EXTENSION))
				{
					out = fileNameNoExtension + ENCRYPTED_CONFIG_FILE_EXTENSION;
					System.out.println( "Encrypt file=" + path + " To " + out + ((encryptConfigFile(path, out))?" successfully":" failed"));
				}
				else if (fileExtensionName.equals(ENCRYPTED_CONFIG_FILE_EXTENSION))
				{
					System.out.println( "ignore encrypted file=" + path);
				}
				else
				{
					System.out.println( "Error: extension of file must be " + CONFIG_FILE_EXTENSION);
				}

			break;

			default:
				System.out.println( "Error: unknown argument! " + work);
			break;
		}
	}

	private static boolean encryptConfigFile(String inFilename, String outFilename)
	{
		FileOutputStream out = null;
		File in = null;
		BufferedReader reader = null;

		try
		{
			out = new FileOutputStream(new File(outFilename));
			in = new File(inFilename);

			reader = new BufferedReader(new FileReader(in));
			String tempString = null;
			int length = 0;
			int newlength = 0;
			int padSize = 0;
			while ((tempString = reader.readLine()) != null)
			{
				//System.out.println( "tempString=" + tempString);
				String line = tempString + '\n';
				length = line.length();
				//newlength = (length / 8 + 1) * 8;
				//padSize = newlength - length;
				//line = padRight(line, newlength, (char)padSize);
				//System.out.println("newlength=" + newlength + ", length=" + length + ", line=" + line);
				try
				{
					final byte[] cipherbytes = TripleDES.encrypt(line);
					length = cipherbytes.length;
					//System.out.println("length=" + length);

					byte[] bytes = ByteBuffer.allocate(4).putInt(length).array();

					if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
					{
						out.write(bytes);
					}
					else
					{
						out.write(bytes[3]);
						out.write(bytes[2]);
						out.write(bytes[1]);
						out.write(bytes[0]);
					}
					out.write(cipherbytes);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
			}
			reader.close();
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (reader != null)
			{
				try {reader.close();}
				catch (IOException e) {}
			}
			if (out != null)
			{
				try {out.close();}
				catch (IOException e) {}
			}
		}

		return true;
	}

	private static boolean decryptConfigFile(String inFilename, String outFilename)
	{
		FileOutputStream out = null;
        File in = null;
        Long filelength = 0L;
        byte[] filecontent;
        try
		{
			out = new FileOutputStream(new File(outFilename));
			in = new File(inFilename);
			filelength = in.length();
			filecontent = new byte[filelength.intValue()];

            FileInputStream fis = new FileInputStream(in);
            fis.read(filecontent);
            fis.close();
        }
		catch (FileNotFoundException e)
		{
            e.printStackTrace();
			return false;
        }
		catch (IOException e)
        {
            e.printStackTrace();
			return false;
        }
		finally
		{
		}

		long WordSize = 4;
		long Offset = 0;
		long BlockLength = 0;

		while (Offset < filelength)
		{
			byte[] b = new byte[4];
			b[0] = filecontent[(int)Offset];
			b[1] = filecontent[(int)Offset+1];
			b[2] = filecontent[(int)Offset+2];
			b[3] = filecontent[(int)Offset+3];

			if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
			{
				BlockLength = (((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8) | (b[3] & 0xff));
			}
			else
			{
				BlockLength = (((b[3] & 0xff) << 24) | ((b[2] & 0xff) << 16) | ((b[1] & 0xff) << 8) | (b[0] & 0xff));
			}
			//System.out.println("BlockLength=" + BlockLength);
			Offset += WordSize;
			byte[] Data = new byte[(int)BlockLength];
			for (int i = 0; i<BlockLength; i++)
			{
				Data[i] = filecontent[(int)Offset+i];
			}

			String plainText;
			try
			{
				plainText = TripleDES.decrypt(Data);
				out.write(plainText.getBytes("utf-8"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}

			Offset += BlockLength;
		}
		if (null != out)
		{
			try {out.close();}
			catch (IOException e) {}
		}

		return true;
	}

	private static String getExtensionName(String filename)
	{
		if ((filename != null) && (filename.length() > 0))
		{
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length() - 1)))
			{
				return filename.substring(dot);
			}
		}
		return filename;
	}

	private static String getFileNameNoEx(String filename)
	{
		if ((filename != null) && (filename.length() > 0))
		{
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length())))
			{
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	private static String padLeft(String src, int len, char ch) {
		int diff = len - src.length();
		if (diff <= 0) {
			return src;
		}

		char[] charr = new char[len];
		System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
		for (int i = 0; i < diff; i++) {
			charr[i] = ch;
		}
		return new String(charr);
	}

	private static String padRight(String src, int len, char ch) {
		int diff = len - src.length();
		if (diff <= 0) {
			return src;
		}

		char[] charr = new char[len];
		System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
		for (int i = src.length(); i < len; i++) {
			charr[i] = ch;
		}
		return new String(charr);
	}

	private static byte[] intToByteArray(int i)// throws Exception
	{
		try
		{
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			DataOutputStream dos= new DataOutputStream(buf);
			dos.writeInt(i);
			byte[] b = buf.toByteArray();
			dos.close();
			buf.close();

			return b;
		}
		catch (Exception e)
		{
		}

		return null;
	}

	private static int ByteArrayToInt(byte b[])// throws Exception
	{
		try
		{
			int temp = 0, a=0;
			ByteArrayInputStream buf = new ByteArrayInputStream(b);
			DataInputStream dis= new DataInputStream (buf);

			return dis.readInt();
		}
		catch (Exception e)
		{
		}

		return 0;
	}

}

