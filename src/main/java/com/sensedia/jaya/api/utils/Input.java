package com.sensedia.jaya.api.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Input {

	private InputStream is;
	private String encoding = "UTF-8";
	private boolean closeInput = true;

	public String getEncoding() {
		return encoding;
	}

	public boolean getCloseInput() {
		return this.closeInput;
	}

	public Input setCloseInput( boolean value ) {
		this.closeInput = value;
		return this;
	}

	public Input setEncoding( String encoding ) {
		this.encoding = encoding;
		return this;
	}

	private Input( InputStream is ) {
		this.is = is;
	}

	public static Input file( String filename ) {
		return file( filename, false );
	}

	public static Input file( String filename, boolean ignoreMissing ) {
		try {
			return new Input( new FileInputStream( filename ) );
		} catch ( FileNotFoundException e ) {
			if ( ! ignoreMissing )
				throw new RuntimeException( e );
			return new Input( null );
		}
	}

	public static Input file( File f ) {
		return file( f , false );
	}

	public static Input file( File f, boolean ignoreMissing ) {
		try {
			return new Input( new FileInputStream( f ) );
		} catch ( FileNotFoundException e ) {
			if ( !ignoreMissing)
				throw new RuntimeException( e );
			return new Input(null);
		}
	}

	public static Input resource( String resource ) {
		return resource( resource, false );
	}

	public static Input resource( String resource, boolean ignoreMissing ) {
		InputStream is = Input.class.getClassLoader().getResourceAsStream( resource );
		if ( is == null && !ignoreMissing )
			throw new RuntimeException( "Cannot find resource in classpath with name '" + resource + "'" );
		return new Input( is );
	}

	public static Input stream( InputStream is ) {
		return new Input( is );
	}

	public static Input string( String s ) {
		return new Input( new ByteArrayInputStream( s.getBytes() ) );
	}

	public static Input string( String s, String encoding ) {
		try {
			Input result = new Input( new ByteArrayInputStream( s.getBytes( encoding ) ) );
			result.setEncoding( encoding );
			return result;
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
	}

	public static Input bytes( byte[] bytes ) {
		if ( bytes == null ) return new Input(null);
		return new Input( new ByteArrayInputStream( bytes ) );
	}

	public static Input fileOrResource( String name ) {
		return fileOrResource( name, false );
	}

	@SuppressWarnings("resource")
	public static Input fileOrResource( String name, boolean ignoreMissing ) {
		InputStream is = null;
		try {
			is = new FileInputStream( name );
		} catch ( FileNotFoundException e ) {
		}

		if ( is == null ) {
			is = Input.class.getClassLoader().getResourceAsStream( name );
		}

		if ( is == null && !ignoreMissing ) {
			throw new RuntimeException( "Cannot find resource or file name '" + name + "'" );
		}
		return new Input( is );
	}

	public String readString() {
		if ( this.is == null ) return null;
		try {
			return new String( readBytes(), this.encoding );
		} catch ( UnsupportedEncodingException e ) {
			throw new RuntimeException( e );
		}
	}

	public List<String> readLines() {
		if ( this.is == null ) return new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( is, this.encoding ) );
			List<String> list = new ArrayList<String>();
			String line = reader.readLine();
			while ( line != null ) {
				list.add( line );
				line = reader.readLine();
			}

			if ( closeInput ) closeQuietly( this.is );
			return list;
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
	}

	public byte[] readBytes() {
		if ( this.is == null ) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		copy( this.is, baos );
		if ( closeInput ) closeQuietly( this.is );
		return baos.toByteArray();
	}

	public InputStream getStream() {
		return is;
	}

	public long copy( OutputStream out ) {
		if ( this.is == null ) return 0;
		long result = copy( this.is, out );
		if ( closeInput ) closeQuietly( this.is );
		return result;
	}

	public static void closeQuietly( InputStream is ) {
		if ( is == null ) return;
		try {
			is.close();
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
	}

	public long writeFile( File f ) {
		if ( this.is == null ) return 0;
		try {
			return copy( new FileOutputStream( f ) );
		} catch ( FileNotFoundException e ) {
			throw new RuntimeException( e );
		}
	}

	public long writeFileMkdir( File f ) {
		if ( this.is == null ) return 0;
		if ( ! f.getAbsoluteFile().getParentFile().exists() ) {
			f.getAbsoluteFile().getParentFile().mkdirs();
		}
		return writeFile( f );
	}

	public long writeFile( String filename ) {
		return writeFile( new File( filename ) );
	}

	public long writeFileMkdir( String filename ) {
		return writeFileMkdir( new File( filename ) );
	}

	public static long copy( InputStream input, OutputStream output ) {
		if ( input == null ) return 0;
		try {
			byte[] buffer = new byte[1024 * 4];
			long count = 0;
			int n = 0;
			while ( -1 != ( n = input.read( buffer ) ) ) {
				output.write( buffer, 0, n );
				count += n;
			}

			return count;
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}
	}
}
