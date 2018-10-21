package pg.ppabis.sbd2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Script {

    private String[] script;

    public Script(String file) throws FileNotFoundException, IOException {
    	if(file.equalsIgnoreCase("!")) {
    		interactive();
    		return;
    	}
        File f = new File(file);
        if(f.exists()) {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            List<String> lines = new ArrayList<String>();
            String l = null;
            while((l = br.readLine()) != null)
                lines.add(l);
            br.close();

            script = lines.toArray(new String[lines.size()]);
        } else {
            throw new FileNotFoundException("File "+file+" not found.");
        }
    }

    String export_command_buffer = "";
    
    public void parseLine(String l, int i) {
    	export_command_buffer+=l+"<br>";
    	String[] p = l.split(" ");
        switch(p[0]) {
            case "find":
                if(p.length<=1) {
                    System.err.println("Malformed line "+(i+1)+": "+l);
                } else {
                    int id = Integer.parseInt(p[1]);
                    Main.find(id);
                }
                break;
            case "insert":
            	if(p.length<=2) {
            		System.err.println("Malformed line "+(i+1)+": "+l);
            	} else {
            		int id = Integer.parseInt(p[1]);
            		byte[] data = p[2].getBytes();
            		Main.insertRecord(id, data);
            	}
            	if(Page.overflowRecords >= Index.indexes.length * Page.OVERFLOWS_PER_PAGE) Page.reorder();
                break;
            case "delete":
                if(p.length<=1) {
                    System.err.println("Malformed line "+(i+1)+": "+l);
                } else {
                    int id = Integer.parseInt(p[1]);
                    Main.delete(id);
                }
                break;
            case "edit":
                if(p.length<=2) {
                    System.err.println("Malformed line "+(i+1)+": "+l);
                } else {
                    int id = Integer.parseInt(p[1]);
                    Main.editData(id, p[2].getBytes());
                }
                break;
            case "editid":
                if(p.length<=2) {
                    System.err.println("Malformed line "+(i+1)+": "+l);
                } else {
                    int id = Integer.parseInt(p[1]);
                    Main.editId(id, Integer.parseInt(p[2]));
                }
                break;
            case "export":
            	if(p.length<=1) {
                    System.err.println("Malformed line "+(i+1)+": "+l);
                } else {
                    Main.export(p[1], export_command_buffer);
                    export_command_buffer = "";
                }
            	break;
            case "reorder":
                Page.reorder();
                break;
            case "print":
                Main.printDb(-1, -1, new int[]{-1, -1}, true);
                break;
        }
    }
    
    public void run() {
        for(int i=0;i<script.length;++i) {
            String l = script[i];
            parseLine(l,i);
        }
    }
    
    public void interactive() throws IOException {
    	BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
    	boolean run = true;
    	while(run) {
    		String line = r.readLine();
    		if(line.equalsIgnoreCase("quit")) run=false;
    		else parseLine(line, 0);
    	}
    	r.close();
    }

}
