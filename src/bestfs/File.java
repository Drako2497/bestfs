package bestfs;

import java.util.ArrayList;

public class File {
	boolean isFolder;
	int fileID;
	String name;
	String type;
	String owner;
	int numBlocksUsed;
	int bytesSize;
	String permission;
	File parentDir;
	ArrayList<File> childrenDir;
	
	public File(boolean isFolder, int fileID, String name, String owner,
			String permission, File parentDir) {
		this.isFolder = isFolder;
		this.fileID = fileID;
		this.name = name;
		this.owner = owner;
		this.permission = permission;
		this.parentDir = parentDir;
		childrenDir = new ArrayList<File>();
	}
	
	public File(boolean isFolder, int fileID, String name, String type, String owner, 
			int numBlocksUsed, int bytesSize, String permission, File parentDir) {
		this.isFolder = isFolder;
		this.fileID = fileID;
		this.name = name;
		this.type = type;
		this.owner = owner;
		this.numBlocksUsed = numBlocksUsed;
		this.bytesSize = bytesSize;
		this.permission = permission;
		this.parentDir = parentDir;
		childrenDir = new ArrayList<File>();
	}
}