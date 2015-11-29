package bestfs;

import java.util.ArrayList;

public class BestFS {
	Block[] diskspace;
	int[] listOfFreeSpace;
	int indexOfLastFreeSpace;
	int fileID = 0;
	File root;
	ArrayList<File> currentDir = new ArrayList<File>();
	
	private void currentd() {
		String res = "";
		for(int i = 0; i < currentDir.size(); i++) {
			res += currentDir.get(i).name + "/";
		}
		System.out.println(res);
	}
	
	private void maked(String dname) {
		File newDir = new File(true, fileID, dname, "tony", "rw", currentDir.get(currentDir.size()-1));
		overhead(newDir);
	}
	
	private void createf(String file) {
		File newFile = new File(false, fileID, file, "word", "tony", 1, 1000, "r", currentDir.get(currentDir.size()-1));
		
		if(isFreeSpace(newFile.bytesSize)) {
			fillFreeSpace(newFile.bytesSize);
			fillBlocks(newFile.bytesSize, newFile.fileID, newFile);
			overhead(newFile);
		} else {
			System.out.println("ERROR: No more free space on disk.");
		}
	}
	
	private void extendf(String file, int size) {
		File foundFile = findFile(file);
		if(foundFile != null && isFreeSpace(size)) {
			fillFreeSpace(size);
			fillBlocks(size, foundFile.fileID, foundFile);
			foundFile.bytesSize += size;
		}
	}
	
	private void truncf(String file, int size) {
		File foundFile = findFile(file);
		if(foundFile != null && size <= foundFile.bytesSize) {
			truncFreeSpace(size);
			truncBlocks(size, foundFile.fileID, foundFile);
			foundFile.bytesSize -= size;
		}
	}
	
	private void formatd() {
		diskspace = new Block[250];
		int diskSize = diskspace.length;
		for(int i = 0; i < diskSize; i++) {
			diskspace[i] = new Block();
			int blockSize = diskspace[i].blockData.length;
			for(int j = 0; j < blockSize; j++) {
				diskspace[i].blockData[j] = -1;
			}
		}
		
		listOfFreeSpace = new int[1000000];
		for(int i = 0; i < listOfFreeSpace.length; i++) {
			listOfFreeSpace[i] = -1;
		}
		
		indexOfLastFreeSpace = 0;
		fileID = 0;
		currentDir = new ArrayList<File>();
		initCurDir();
	}
	
	private void chdir(String dir) {
		File foundFile = findFile(dir);
		ArrayList<File> reverseDir = new ArrayList<File>();
		
		if(foundFile.isFolder) {
			reverseDir.add(foundFile);
			reverseDir = traverseParents(foundFile, reverseDir);

			while(!reverseDir.isEmpty()) {
				currentDir.add(reverseDir.remove(reverseDir.size()-1));
			}
		} else {
			System.out.println("chdir: Not like this...");
		}
	}
	
	private void initCurDir() {
		root = new File(true, fileID, ".", "tony", "rw", null);
		fileID++;
		currentDir.add(root);
	}
	
	private void overhead(File file) {
		File parent = currentDir.get(currentDir.size()-1);
		parent.childrenDir.add(file);
		fileID++;
	}
	
	private boolean isFreeSpace(int fileSize) {
		int availSpace = 0;
		for(int i = 0; i < listOfFreeSpace.length; i++) {
			if(listOfFreeSpace[i] == -1) {
				availSpace++;
			}
		}
		
		if(availSpace >= fileSize) {
			return true;
		} else {
			return false;
		}
	}
	
	private void fillFreeSpace(int fileSize) {
		int tmp = indexOfLastFreeSpace;
		for(int i = tmp; i < fileSize; i++) {
			if(listOfFreeSpace[i] == -1) {
				listOfFreeSpace[i] = 1;
				indexOfLastFreeSpace++;
			}
		}
	}
	
	private void truncFreeSpace(int fileSize) {
		int tmp = indexOfLastFreeSpace;
		for(int i = tmp; i >= 0; i--) {
			if(listOfFreeSpace[i] == 1) {
				listOfFreeSpace[i] = -1;
				indexOfLastFreeSpace--;
			}
		}
	}
	
	private void fillBlocks(int size, int fileID, File file) {
		int fileSize = size;
		int newFileID = fileID;
		int blocksUsed = 0;
		
		int diskSize = diskspace.length;
		int blockSize = diskspace[0].blockData.length;
		for(int i = 0; i < diskSize && fileSize != 0; i++) {
			blocksUsed++; // this is a problem
			for(int j = 0; j < blockSize && fileSize != 0; j++) {
				if(diskspace[i].blockData[j] == -1) {
					diskspace[i].blockData[j] = newFileID;
					fileSize--;
				}
			}
		}
		
		file.numBlocksUsed += blocksUsed;
	}
	
	private void truncBlocks(int size, int fileID, File file) {
		int fileSize = size;
		int newFileID = fileID;
		int blocksUsed = 0;
		
		int diskSize = diskspace.length;
		int blockSize = diskspace[0].blockData.length;
		for(int i = 0; i < diskSize && fileSize != 0; i++) {
			for(int j = 0; j < blockSize && fileSize != 0; j++) {
				if(diskspace[i].blockData[j] == newFileID) {
					diskspace[i].blockData[j] = -1;
					fileSize--;
				}
			}
			blocksUsed++; // this is a problem - might need to keep an array of blockids
		}
		
		file.numBlocksUsed -= blocksUsed;
	}
	
	private File findFile(String name) {
		if(name.toLowerCase().equals(root.name)) {
			return root;
		} else if(!root.childrenDir.isEmpty()) {
			return findFileHelper(name, root);
		} else {
			return null;
		}
	}
	
	private File findFileHelper(String name, File f) {
		for(File child : f.childrenDir) {
			if(child.name.toLowerCase().equals(name.toLowerCase())){
				return child;
			} 
			if (!child.childrenDir.isEmpty()) {
				findFileHelper(name, child);
			}
		}
		return null;
	}
	
	private ArrayList<File> traverseParents(File file, ArrayList<File> listOfParents) {
		ArrayList<File> tmp = new ArrayList<>();
		if(file.parentDir.fileID == 0) {
			return listOfParents;
		} else if (file.parentDir.fileID != 0) {
			listOfParents.add(file.parentDir);
			traverseParents(file.parentDir, listOfParents);
			return tmp;
		} else {
			return null;
		}
	}
	
	private void initialize() {
		formatd();
		currentd();
		maked("testdir");
		createf("testfile");
		extendf("testfile", 100);
		truncf("testfile", 200);
		createf("testfile2");
		extendf("testfile2", 200);
		truncf("testfile2", 400);
		chdir("testdir");
		currentd();
		formatd();
	}
	
	public static void main(String[] args) {
		System.out.println("Hello World!");
		BestFS b = new BestFS();
		b.initialize();
	}
}
