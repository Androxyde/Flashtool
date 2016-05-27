/*******************************************************************************
 * Copyright (c) 2004 Robert K"opferl
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * or in the file license.html
 * 
 * Contributors:
 *      Robert K"opferl <quellkode@koepferl.de> - Initial writing
 * Created:
 * 		16.02.2004
 * Project:
 *      SWT-Hexedit
 * Version:
 * 		$Version$
 * Last Change:
 *      $Log$ 
 *******************************************************************************/
package swt_components;

 
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;




/**
 * @author KOEPFERR
 * 
 * This class implements a nice Hex-control to be used
 * in a pure SWT application. It provides an interface
 * simmilar to these known from other SWT controls. Since
 * SWT doesn't allow ineritance of class StyledText, this 
 * control is a compositoin of a StyledText object and
 * glue code around to change the behavior.
 */
public class SwtHexEdit extends Composite implements FocusListener 
{
	private int topindex = 0;
    // fields internal use.
	protected StyledText hexpart;
	// insert or overwrite mode
	protected boolean insertMode = false;
	protected int numBytesPerLine =8;
	protected int numBytesGrouped =4;
	protected int numLines =4;
	protected char hexByteDelimiter = ' ';
	protected char hexGroupDelimiter = '-';
	protected Font monofont = null;
	// internal buffer for the hexdata 
	protected byte[] byteData = null;
	// actual lenght of the buffer
	protected int dataLength = 0;
	//nibbleposition of the cursor. So nibblePosition/2 is the byte position
	protected int nibblePosition = 0;
	// "global" Clipboard
	protected Clipboard cb =null;

	
	/**
	 * Hexedit implements a Control for editing hex data.
	 * It is able to show and edit a tabbed nibble view and/or
	 * an ASCII view.
	 * Hexedit is only able to work on buffers with constant length.
	 * So the initial buffersize has to be provided. However it is possible
	 * to change the buffer afterwards.
	 * @param parent parent composite where to insert
	 * @param style The composite's style argument use NO_ASCII to prevent
	 * the Ascii part from showing up.
	 * @param buffersize initial buffer
	 */
	public SwtHexEdit(Composite parent, int style, int buffersize, int bytesperline, int numoflines, int bytesgrouped )
	{
		super(parent, SWT.NULL);
		
		// set internal parameters
		numBytesPerLine = bytesperline;
		numBytesGrouped = (bytesperline%bytesgrouped==0)? bytesgrouped : bytesperline;
		numLines = numoflines;
		// allocate constant buffer
		byteData = new byte[buffersize];
		// create a font for the control
		FontData fontData = new FontData();
		fontData.height=10;
		fontData.setName("Courier");
		monofont = new Font( getDisplay(), fontData);
		
		// claculate the size of the inner controls
		Point chs = characterSize( monofont );
		
		// decide between Multiline or Singleline
		if( numoflines==1) 
		{	
			style |= SWT.SINGLE;
		}
		else
		{	
			style |= SWT.MULTI;
		}
		// create hexcontrol
		hexpart = new StyledText(this, style );
		hexpart.addVerifyKeyListener( new VerifyKeyListener()
			{
				public void verifyKey(VerifyEvent ev)
				{
					verifyHexKeyEvent(ev);
				}
				
			} );

		hexpart.addMouseListener( new MouseListener(){
            public void mouseDoubleClick(MouseEvent arg0)
            {}
            public void mouseDown(MouseEvent arg0)
            {}
            public void mouseUp(MouseEvent arg0)
            {
            	topindex = hexpart.getTopIndex();
            }});
		
		// create Clipboard
		cb = new Clipboard(this.getDisplay());
        
		hexpart.setFont(monofont);
		addDisposeListener( new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0)
			{
				monofont.dispose();
				cb.dispose();
			}
		});
		
		// Foucs forwarding
		hexpart.addFocusListener(this);
		
		gridDataSetter(monofont);
		//	set layout
		setLayout(new FillLayout());
	}
    
	public SwtHexEdit(Composite parent, int style, int buffersize )
	{
		this( parent, style, buffersize, 8, 5, 8 );
	}
	
	public SwtHexEdit(Composite parent, int style, int buffersize, int bytesperline, int numoflines )
	{
		this( parent, style, buffersize, bytesperline, numoflines, bytesperline );
	}
	
	/*public CgHexEdit(Composite parent, int style, int buffersize, int bytesperline, int numoflines, int bytesgrouped )
	{
		this( parent, style, buffersize, bytesperline, numoflines, bytesgrouped, true );
	}
	*/

    
	/**
	 * Changes the contrl's constant buffer to a new size.
	 * The contents is cut if the new size is less than the current.
	 * If the new size is greater than the current, the trailing bytes
	 * are filled with padding. The number of shown bytes is set depending
	 * on padding. If padding==null, the # of visible chars remains constant
	 * or at maximum newsize. If padding was given, the displayed size becomes
	 * equal to newsize.
	 * @param newsize requested buffer size.
	 * @param padding bytevalue to be used as padding, null is also allowed.
	 */
	public void adjustBuffer( int newsize, Byte padding )
	{
		byte[] newarray = new byte[newsize];
		int end = Math.min( newsize, byteData.length );
		System.arraycopy( byteData, 0, newarray, 0, end );
		// either fill with padding or leave dataLength as it is.
		if( padding != null )
		{
			// fill with padding until the end
			for( int i= end; i<newsize; i++ )
			{
				newarray[i]=padding.byteValue();
			}
			dataLength = newsize;
		}
		else
		{
			dataLength = end;
		}
		byteData = newarray; // reorder
		// ensure cursor stays in visible area
		nibblePosition = Math.min( nibblePosition, byteData.length*2 );
    	
		hexFormatter();
	}
    
	/**
	 * sets the control's buffer to a new content. The buffer remains constant
	 * in its length. The buffer is filled with a copy of the new data.
	 * In case the new data is longer than the buffer, it's truncated to 
	 * the buffer's length, otherwise just as many bytes as given are copied and
	 * the displayed length is set to the minimum of the new length and the 
	 * buffers capacity. If null is given,
	 * the control becomes empty. The buffer lenght becomes zero and nothing
	 * can be enterd. 
	 * @param towhat Content, the control will be set to.
	 * @see adjustBuffer.
	 */
	public void setContent( byte[] towhat )
	{
		if( towhat == null )
		{
			byteData = new byte[0];
			dataLength = 0;
		}
		else
		{
			byte[] hl=towhat;
			dataLength = Math.min(hl.length, byteData.length);
			System.arraycopy( hl, 0, byteData, 0, dataLength);
		}
		nibblePosition = 0;
		// show the data	
		hexFormatter();
	}
    

	
	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's text is modified, by sending
	 * it one of the messages defined in the <code>ModifyListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see ModifyListener
	 * @see removeModifyListener
	 */
	public void addModifyListener (ModifyListener listener) 
	{
		checkWidget ();
		if (listener != null) {
		TypedListener typedListener = new TypedListener (listener);
		addListener (SWT.Modify, typedListener);}
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's text is modified.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see ModifyListener
	 * @see #addModifyListener
	 */
	public void removeModifyListener (ModifyListener listener) 
	{
		checkWidget ();
		if (listener!= null) {
			removeListener( SWT.Modify, listener);
		//if (eventTable == null) return;
		//eventTable.unhook (SWT.Modify, listener);	
		}
	}

	/**
	 * Calls all registered listeners. Does not provide usable event information.
	 * Thus the reciever is required to ask details.
	 */
	private void callModifiedListeners()
	{
		Event ev = new Event();
		ev.data = byteData;
		ev.widget = this;
		ModifyEvent mev = new ModifyEvent(ev);
		notifyListeners(SWT.Modify, ev );

	}


	/**
	 * The intention of this meothod is to source out the calculation
	 * of the sizes of the part controls into an own method which is 
	 * called by constructor and whenever size matters.	 */
	protected void gridDataSetter( Font fnt )
	{
        GridData gd = new GridData();
		Point gress = this.computeSize(-1,-1);
		gd.widthHint = gress.x-4;
		gd.heightHint = gress.y-4;
		hexpart.setLayoutData(gd);
	}

	protected void hexCursorPositioner()
	{
		int cpos;
		// correct nibble Pos if it went out of range
		if( nibblePosition > dataLength*2 || nibblePosition<0)
		{
			cpos = nibblePos2CaretOffsetH( dataLength * 2 );
		}
		else
		{	
			cpos = nibblePos2CaretOffsetH( nibblePosition );
		}
		
		int curLine = hexpart.getLineAtOffset(cpos );
		// decide new topindex
		if( curLine-numLines+1 > topindex ) // current Line under window
		{
			topindex = curLine-numLines+1;
		}
		if( curLine < topindex ) // current Line under window
		{
			topindex = curLine;
		}
		//allways set pos
		hexpart.setCaretOffset( cpos);  
		hexpart.setTopIndex( topindex );
	}
    
	protected void hexFormatter()
	{
		int buffersize = byteData.length*3 ;
		StringBuffer buf = new StringBuffer(buffersize);
		for( int i = 0; i<dataLength; i++ )
		{
			byte currentByte = byteData[i];
			// if currentByte is not the first byte, append separator
			// Convert and append high nibble (bit7 - bit4)
			buf.append(nibble2char(currentByte >> 4));
			// Convert and append low nibble (bit3 - bit0)
			buf.append(nibble2char(currentByte));
			if( (i+1)%numBytesGrouped == 0 )
			{
				if( (i+1) % numBytesPerLine == 0 )
				{
					// insert nl 
					buf.append( hexpart.getLineDelimiter() );
				}
				else
				{
					// space between Groups
					buf.append( hexByteDelimiter );
					buf.append( hexGroupDelimiter );
					buf.append( hexByteDelimiter );
				}
			}
			else
			{
				// space between Bytes
				buf.append( hexByteDelimiter );
			}
		}
		hexpart.setText( buf.toString() );
		hexCursorPositioner();
	}
    
    
	private Point characterSize( Font fnt )
	{
		Point p = new Point(0,0);
		GC g = new GC( this );
		g.setFont( fnt );
		p.x = g.getFontMetrics().getAverageCharWidth();
		p.y = g.getFontMetrics().getHeight();
		g.dispose();
		return p;
	}
    
	
	public Point computeSize(int wHint, int hHint, boolean changed)
	{
		Point p = characterSize(monofont);
		p.y = Math.max( p.y*numLines+4, hHint );
		{
			p.x = ((numBytesPerLine*3)+ (numBytesPerLine/numBytesGrouped*2)) *p.x +4;
			p.x = Math.max( wHint, p.x );		// take the bigger one;
		}
		int charw = monofont.getFontData()[0].getHeight();
		return p;
	}
			             
                         
                         
	/**
	 * Converts a hex digit into an integer (bit3 - bit0)
	 *
	 * @param c hex digit to be converted
	 * @return value of c if it is a hex digit, -1 otherwise
	 */
	private static int char2nibble(char c)
	{
		if (c >= '0' && c <= '9')
		{
			return c - '0';
		}
		if (c >= 'A' && c <= 'F')
		{
			return 10 + c - 'A';
		}
		if (c >= 'a' && c <= 'f')
		{
			return 10 + c - 'a';
		}
    
		return -1;
	}

	/**
	 * Make a nibble (0000-1111) to a character ('0'..'F')
	 * @param nibble
	 * @return
	 */
	protected static char nibble2char(int nibble)
	{
		// Filter the low nibble (we know then that 0 <= nibble < 16!)
		nibble &= 0x0F;

		// Convert to '0' - '9' or 'A' - 'F'
		if (nibble < 10)
		{
			return (char)('0' + nibble);
		}
		else
		{
			return (char)('A' + nibble - 10);
		}
	}


	/**
	 * Determines the position of the caret relative to the textbytes
	 * DE AD BE EF - 38 48 55
	 * ^   ^   ^   ^^
	 * |   |   |    returns 3,4
	 * |   |    returns 2
	 * |    returns 1
	 *  returns 0
	 * @param cp caret position.
	 * @return see desc.
	 */
	protected int getCaretByteRelativePositionH( int cp )
	{
		int charsperline = numBytesPerLine*3 + 2*(numBytesPerLine / numBytesGrouped) -3;
		int p = cp% (charsperline+hexpart.getLineDelimiter().length());
		int groups = numBytesPerLine / numBytesGrouped;
		int blockpos = p%(numBytesGrouped*3+2);
		int retval = blockpos %3;
		if( (blockpos >= numBytesGrouped*3) )
		{
			retval = blockpos - numBytesGrouped*3 +3;
		}
		return retval;
	}
    
	protected int charsPerLineH()
	{
		return numBytesPerLine*3 + 2*(numBytesPerLine / numBytesGrouped) -3
		+hexpart.getLineDelimiter().length();
	}
    
	/**
	 * converts a giben nibble position into corresponging caret offset
	 * concerning a hexedit part.
	 * @param np
	 * @return
	 */
	protected int nibblePos2CaretOffsetH( int np )
	{
		int charsperline = charsPerLineH();
		int bytepos = np/2;
		int line = bytepos/numBytesPerLine;
		int lpos = bytepos%numBytesPerLine;
		int co = line*charsperline + lpos*3 + (lpos/numBytesGrouped)*2;
		return co + np%2;  // add the halfbyte count  
	}
    
	/**
	 * Depending on the caret position (cp) given, the function
	 * returns the corresponding byte index fpr the intenal buffer.
	 * Concerns the hex part.
	 * @param cp caret position in question.
	 * @return index in buffer.
	 */
	protected int caret2BytePositionH( int cp )
	{
//		int charsperline = charsPerLine();    Do not use this variant ! ??
		int charsperline = numBytesPerLine*3 + 2*(numBytesPerLine / numBytesGrouped) -3;
		int p = cp% (charsperline+hexpart.getLineDelimiter().length());
		int line = cp / (charsperline+hexpart.getLineDelimiter().length());
		int groups = p / (numBytesGrouped*3+2);
		int bpos=line*numBytesPerLine+  (p-(groups*2))/3; 
		return bpos;
	}
    
	public void verifyHexKeyEvent(VerifyEvent ev)
	{
		if (hexpart.getEditable()) {
		// find relative caret position in the hex part
		int relpos = getCaretByteRelativePositionH(hexpart.getCaretOffset());
		int bpos = caret2BytePositionH(hexpart.getCaretOffset());
		nibblePosition = bpos*2 + relpos;
		// no action
		ev.doit = false;
		
		// test for hexdigits
		if (ev.character >= '0'
			&& ev.character <= '9'
			|| ev.character >= 'A'
			&& ev.character <= 'F'
			|| ev.character >= 'a'
			&& ev.character <= 'f')
		{

			// insert or overwrite a byte
			
			if( insertMode )
			{
				// vor einem Byte: insert a byte into the array
				if(relpos==0)
				{
					insertByte( bpos, (byte) (char2nibble(ev.character) << 4) );
				}   // inside a byte, set the nibble
				else if(relpos==1)
				{
					byteData[bpos] &= 0xF0;
					byteData[bpos] |= (byte)(char2nibble(ev.character));
				}
				// other positions don't matter        		
			}
			else // overwrite
			{
				// preserve array bounds
				if( bpos < byteData.length )
				{
					// bevore Byte: write high nibble
					if(relpos==0)
					{
						byteData[bpos] &= 0x0F;
						byteData[bpos] |= (byte)(char2nibble(ev.character)<<4);
					}   // inside a byte, write lo nibble
					else if(relpos==1)
					{
						byteData[bpos] &= 0xF0;
						byteData[bpos] |= (byte)(char2nibble(ev.character));
					}        	
				}
			}
			nibblePosition = Math.min( nibblePosition+1, byteData.length*2 );
			callModifiedListeners();
			hexFormatter();

		}
        
		// Special chars
		else switch (ev.character)
		{
			case 0 :
			case ' ' : // space
				{
					nibblePosition++;
				}
				break;
			case '\u0008' : // back space
				if( relpos == 0 && nibblePosition > 0)
				{
					delByte( Math.max((nibblePosition/2-1),0));
					nibblePosition -= 2;
				}
				else
				{
					delByte( nibblePosition / 2 );
					nibblePosition -= 1;
				}
				callModifiedListeners();
				hexFormatter();
				break;
			case '\u007f' : // delete
				{
					delByte( nibblePosition/2 );
				}
				callModifiedListeners();
				hexFormatter();
				break;
		}
        
		// Shift+ Arrow,ins,del,etc
		if (ev.stateMask == 131072 && ev.character == 0)
		{
		}

		// Strg+x
		if (ev.stateMask == 262144 && ev.character == 24)
		{
			// cut out the byte sub array, copy to clipb, repaint
			TextTransfer transfer = TextTransfer.getInstance();
			Point sel = hexpart.getSelection();
			int von = caret2BytePositionH( sel.x );
			int bis = caret2BytePositionH( sel.y+1 );
			byte[] buf = new byte[bis-von];
			System.arraycopy( byteData, von, buf, 0, bis-von );
			cb.setContents(new Object[]{ByteArray.bytearray2string(buf)}, new TextTransfer[]{transfer});
			// cut out:
			System.arraycopy( byteData, bis, byteData, von, byteData.length-bis );
			dataLength = Math.max( dataLength-(bis-von), 0 );	// keep minimum l.			
			hexFormatter();
		}
        
		// Strg+C   ||   Strg+ins
		if (ev.stateMask == 262144 && ev.character == 3 ||
			ev.stateMask == 262144  && ev.keyCode == 16777225 )
		{
			// copy the selected bytes and transform into string
			TextTransfer transfer = TextTransfer.getInstance();
			Point sel = hexpart.getSelection();
			int von = caret2BytePositionH( sel.x );
			int bis = caret2BytePositionH( sel.y+1 );
			byte[] buf = new byte[bis-von];
			System.arraycopy( byteData, von, buf, 0, bis-von );
			cb.setContents(new Object[]{ByteArray.bytearray2string(buf)}, new TextTransfer[]{transfer});
		}
		
        
        
		// Pressed STRG+V  |   Shift+ins
		if (ev.stateMask == 262144 && ev.character == 22||
			ev.stateMask == 131072  && ev.keyCode == 16777225 )
		{
			// paste a hexstring into the data array
			TextTransfer transfer = TextTransfer.getInstance();
			String cbdata = (String)cb.getContents(transfer);
			
			if (byteData != null) 
			{
				int np2 = nibblePosition/2;  // is byte position
				byte[] insdta = ByteArray.string2bytearray(cbdata);
				if( insertMode )
				{
					// move the content, make a gap
					System.arraycopy( byteData, np2, byteData, Math.min( np2+ insdta.length, byteData.length), 
									Math.max(byteData.length-(np2+insdta.length), 0) );
					// correct dataLength
					dataLength = Math.min( dataLength + insdta.length, byteData.length );
				}
				// fill the gap or notgap with insdta
				System.arraycopy( insdta, 0, byteData, np2, 
										Math.min(insdta.length, byteData.length-np2-0) );
				hexFormatter();
			}

		}
        
		switch( ev.keyCode )
		{
			// <- left Arrow
			case 16777219:
				{
					int cp = hexpart.getCaretOffset()-1, inc=0;
					while(getCaretByteRelativePositionH(cp-inc)>=2 && inc<5)
					{
						inc++;
					}
					hexpart.setCaretOffset(cp-inc);
					if( nibblePosition>0) 
						nibblePosition -= 1;
				}
				break;
			
			// -->
			case 16777220:
				{
					int cp = hexpart.getCaretOffset()+1, inc=0;
					while(getCaretByteRelativePositionH(cp+inc)>=2 && inc<5)
					{
						inc++;
					}
					hexpart.setCaretOffset(cp+inc);
					if( nibblePosition < dataLength*2)
						nibblePosition += 1;
					
				}
				break;
			
			// ^ arrow up
			case 16777217:
				if(nibblePosition > numBytesPerLine*2-1)
				{
					nibblePosition -= numBytesPerLine*2+1;
					hexCursorPositioner();
				}
			break;
			
			// v Arrow dn 
			case 16777218:
				if(nibblePosition+numBytesPerLine*2-1 < dataLength*2 )
				{
					nibblePosition += numBytesPerLine*2-1;
					hexCursorPositioner();
				}
				break;
			
			// Pressed Ins
			case 16777225:
			{
				insertMode = !insertMode;
			}
			break;
			
			case 16777223:  // Pos1
			case 16777224:  // Ende
				ev.doit = true;
				break;
		}
		}
	}

	
	/** 
	 * Inserts a byte b at the given pos into data.
	 * @param pos where to insert.
	 * @param b byte to be inserted.
	 */
	protected void insertByte(int pos, byte b )
	{
		if( pos < byteData.length )
		{
			System.arraycopy( byteData, pos, byteData, pos+1, byteData.length-pos-1 );
			byteData[pos] = b;
			dataLength = Math.min( dataLength+1, byteData.length );
			// call listeners due to something has changed
		}
	}
    
	protected void delByte( int pos )
	{
		if( pos < byteData.length )
		{
			System.arraycopy( byteData, pos+1, byteData, pos, byteData.length-pos-1);
			dataLength = Math.max( 0, dataLength-1 );
			// call listeners due to something has changed
		}
	}
    

	/**
	 * Retrieves the number of bytes that are grouped together.
	 * A multiple of this number must be equal to genNumBytesPerLine(). 
	 * @return
	 */
	public int getNumBytesGrouped()
	{
		return numBytesGrouped;
	}

	/**
	 * Retrieves the number of bytes that are shown in each line.
	 * Lines are filled with groups of numBytesGrouped Bytes
	 * @return
	 */
	public int getNumBytesPerLine()
	{
		return numBytesPerLine;
	}

	/**
	 * Sets the number of Bytes that are grouped inside a line.
	 * getNumBytesPerLine() must be an integer multiple of this number.
	 * Otherwise the results are unpredictable. Setting these two equal
	 * removes the grouping.
	 * @param i
	 */
	public void setNumBytesGrouped(int i)
	{
		numBytesGrouped = i;  
//		repaint
		hexFormatter();
	}

	/**
	 * Sets the number of bytes that are shown in each line.
	 * This has to be an integer multiple of getNumBytesGrouped(),
	 * otherwise the results are unpredictable. Setting these two equal
	 * removes the grouping.
	 * @param i
	 */
	public void setNumBytesPerLine(int i)
	{
		numBytesPerLine = i;
//		repaint
		hexFormatter();
	}

	/**
	 * @return true, if the control is in a mode where new bytes are insertet in place and 
	 * these behind are shiftet further, false if in overwrite mode. 
	 */
	public boolean isInsertMode()
	{
		return insertMode;
	}

	/**
	 * Set the mode of the control. Set to true means that existing bytes are shiftet towards
	 * and over the end of the buffer while new ones are insertet. Set to false means that
	 * the control is in overwrite mode. Newly entered bytes overwrite existing ones.
	 * @param b
	 */
	public void setInsertMode(boolean b)
	{
		insertMode = b;
	}

	/**
	 * Retrieves the current length of visible data in the control.
	 * Remember to distinguish between the lenght of the byte buffer and the actual
	 * lenght of valid data. This is the zero based length of the shown data. It
	 * can't exceed the buffer's length.
	 * @return number of bytes of entered data.
	 * @see adjustBuffer.
	 */
	public int getVisibleLength()
	{
		return dataLength;
	}


	/**
	 * Sets the current length of visible data in the control. So reduces the info displayed.
	 * Remember to distinguish between the lenght of the byte buffer and the actual
	 * lenght of valid data. This is the zero based length of the shown data. It
	 * can't exceed the buffer's length.
	 * @param nl
	 * @see adjustBuffer.
	 */
	public void setVisibleLength(int nl)
	{
		if( nl <= byteData.length )
		{
			dataLength = nl;
			hexFormatter();
		}
	}

	/**
	 * Retrieves the actual byte buffer of the control. Thus you can directly 
	 * access the bytes in the buffer. Remember that changes are not reflected
	 * to the conrol until you for example call setByteData. 
	 * The array is not copied, just returned as it is.
	 * @return the byte array of the actual data.
	 * @see setByteData, adjustBuffer
	 */
	public byte[] getByteData()
	{
		return byteData;
	}
	
	/**
	 * Int versoin of getByteData
	 * @see getByteData
	 * @return
	 */
	public int getByteDataI()
	{
		int ret = 0;
		if( byteData.length > 0 )  ret += byteData[0]*0x1;
		if( byteData.length > 1 )  ret += byteData[1]*0x100;
		if( byteData.length > 2 )  ret += byteData[2]*0x10000;
		if( byteData.length > 3 )  ret += byteData[3]*0x1000000;
		return ret;
	}

	/**
	 * Sets the controls byte buffer to a new one. Surounding variables are 
	 * set appropriately. The Caret is positioned equal or at maximum possible.
	 * The dataLength, which describes the number of bytes displayed is set
	 * equal to the newly given data, so that all byts are displayed.
	 * The array is not copied, just taken as it is.
	 * @param bs a byte array to which the buffer shall be set to.
	 */
	public void setByteData(byte[] bs)
	{
		byteData = bs;
		nibblePosition = Math.min( byteData.length*2, nibblePosition );
		dataLength = byteData.length;
		hexFormatter();
	}

	/**
	 * same as setByteData( byte[] ), however interpreting bool as byte[1]
	 * @param bs
	 */
	public void setByteData(int b)
	{
		byteData = new byte[]{ (byte)((b>>0)&0xFF), (byte)((b>>8)&0xFF), (byte)((b>>16)&0xFF), (byte)((b>>24)&0xFF)  };
		setByteData(byteData);
	}


	/**
	 * same as setByteData( byte[] ), however interpreting short as byte[2]
	 * @param bs
	 */
	public void setByteData(short s)
	{
		byteData = new byte[]{ (byte)((s>>0)&0xFF), (byte)((s>>8)&0xFF) };
		setByteData(byteData);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean arg0)
	{
		hexpart.setEnabled( arg0 );
		super.setEnabled(arg0);
	}

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     */
    public void focusGained(FocusEvent arg0)
    {
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
     */
    public void focusLost(FocusEvent arg0)
    {
		Event ev = new Event();
		ev.widget = this;
		notifyListeners(SWT.FocusOut, ev );
    }
    
    public void setEditable(boolean editable) {
    	hexpart.setEditable(editable);
    }
}
