package org.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.adb.AdbUtility;
import org.apache.log4j.Logger;

/**
 * Responsible for describing an entry in the program header table
 * @author Christopher Childs
 *
 */
public class ElfProgram {
	/** 4 bytes, tells what kind of segment this array element describes or how to
	 * interpret the array element's information
	 */
	private long p_type;
	private int partnum;
	private ElfProgramType type;
	/** 4 bytes, gives the offset from the beginning of the file at which the first
	 * byte resides
	 */
	private long p_offset;
	/** 4 bytes, gives the virtual address at which the first byte of the segment
	 * resides in memory
	 */
	private long p_vaddr;
	/** 4 bytes, if physical addressing is relevant, this member is reserved for the
	 * segment's physical address
	 */
	private long p_paddr;
	/** 4 bytes, gives the number of bytes in the file image of the segment; may be zero */
	private long p_filesz;
	/** 4 bytes, gives the number of bytes in the memory image of the segment; may be zero */
	private long p_memsz;
	/** 4 bytes, flags relevant to the segment */
	private long p_flags;
	private Set<ElfProgramFlag> flags = new HashSet<ElfProgramFlag>();
	/** 4 bytes, loadable process segments must have congruent values for p_vaddr and
	 * p_offset, modulo page size. Must be a positive power of 2; p_addr should equal p_offset
	 * mod p_align. I don't think I actually verify this anywhere in the code...
	 */
	private long p_align;
	private String ctype;

    /* Nachos variables */
	private static Logger logger = Logger.getLogger(ElfProgram.class);

	public enum ElfProgramType {
		PT_NULL(0),
		PT_LOAD(1),
		PT_DYNAMIC(2),
		PT_INTERP(3),
		PT_NOTE(4),
		PT_SHLIB(5),
		PT_PHDR(6),
		// Processor specific stuff here!
		PT_CERT(558778707);

		private long value;
		
		private ElfProgramType(long value) {
			this.value = value;
		}
		public long getValue() {
			return this.value;
		}

		private static Map<Long, ElfProgramType> longToElfProgramType = 
			new HashMap<Long, ElfProgramType>();

		static {
			for (ElfProgramType f : ElfProgramType.values()) {
				ElfProgramType.longToElfProgramType.put(f.getValue(), f);
			}
		}

		public static ElfProgramType valueToProgramType(long value) {
			return ElfProgramType.longToElfProgramType.get(value);
		}		
	}

	public enum ElfProgramFlag {
		PF_X(1) {
			@Override
			public String toString() {
				return "x";
			}

		},
		PF_W(2) {
			@Override
			public String toString() {
				return "w";
			}
		},
		PF_R(4) {
			@Override
			public String toString() {
				return "r";
			}
		};

		private long value;
		private ElfProgramFlag(long value) {
			this.value = value;
		}
		public long getValue() {
			return this.value;
		}

		abstract public String toString();

		private static Map<Long, ElfProgramFlag> longToElfProgramFlag = 
			new HashMap<Long, ElfProgramFlag>();

		static {
			for (ElfProgramFlag f : ElfProgramFlag.values()) {
				ElfProgramFlag.longToElfProgramFlag.put(f.getValue(), f);
			}
		}

		public static ElfProgramFlag valueToProgramFlag(long value) {
			return ElfProgramFlag.longToElfProgramFlag.get(value);
		}		
	}

	/** The ELF binary with which this program header entry is associated */
	private Elf object;

	public ElfProgram(Elf object, byte programHeaderData[], int partnum) {
		this.object = object;
		this.partnum = partnum;
		parseHeader(programHeaderData);
	}

	private void parseHeader(byte data[]) {
		this.p_type = this.object.elf32_wordToLong(data, 0);
		this.type = ElfProgramType.valueToProgramType(this.p_type);
		this.p_offset = this.object.elf32_wordToLong(data, 4);
		this.p_vaddr = this.object.elf32_wordToLong(data, 8);
		this.p_paddr = this.object.elf32_wordToLong(data, 12);
		this.p_filesz = this.object.elf32_wordToLong(data, 16);
		this.p_memsz = this.object.elf32_wordToLong(data, 20);
		this.p_flags = this.object.elf32_wordToLong(data, 24);
		interpretFlags();
		this.p_align = this.object.elf32_wordToLong(data, 28);
	}

	private void interpretFlags() {
		for (ElfProgramFlag f : ElfProgramFlag.values()) {
			if ((this.p_flags & f.getValue()) == f.getValue()) {
				this.flags.add(f);
			}
		}
	}

	public void setContentType(String ctype) {
		this.ctype = ctype.length()>0?ctype:Integer.toString(partnum);
		printInfo();
	}
	
	public void printInfo() {
		String pname = "Program Name : "+this.ctype;
		String result = "";
		if (pname.length()>=25)
			result = String.format(pname+"\tsize : 0x%08x\tLoad address: 0x%08x", this.p_filesz, this.p_vaddr);
		else
			result = String.format(pname+"\t\tsize : 0x%08x\tLoad address: 0x%08x", this.p_filesz, this.p_vaddr);
		logger.info(result);
		/*System.out.printf("PH entry type: %s\n", this.type);
		System.out.printf("First byte of segment: 0x%08x\tVirtual address: 0x%08x\n", this.p_offset, this.p_vaddr);
		System.out.printf("Physical address: 0x%08x\n", this.p_paddr);
		System.out.printf("File image size: 0x%08x\tMemory image size: 0x%08x\n", this.p_filesz, this.p_memsz);
		System.out.print("Flags: ");
		for (ElfProgramFlag f : this.flags) {
			System.out.print(f);
		}
		System.out.println();
		System.out.println();*/
	}

	public ElfProgramType getType() {
		return this.type;
	}

    public long getProgramSize() {
        return this.p_memsz;
    }
    
    public long getOffset() {
    	return this.p_offset;
    }
    
    public String getFileName() {
    	return object.getFileName()+"."+ctype;
    }

    public String getName() {
    	return object.getName()+"."+ctype;
    }
 
}