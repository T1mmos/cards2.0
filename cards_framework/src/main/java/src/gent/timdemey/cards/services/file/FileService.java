package gent.timdemey.cards.services.file;

import java.io.File;
import java.nio.file.Paths;

import gent.timdemey.cards.services.contract.descriptors.FileDescriptor;
import gent.timdemey.cards.services.contract.descriptors.FileDescriptors;
import gent.timdemey.cards.services.interfaces.IFileService;

public class FileService implements IFileService
{    
    protected String getPath(FileDescriptor fd)
    {
        if (fd == FileDescriptors.USERCFG)
        {
            String filepath = System.getenv("APPDATA") + "/../Local/cards/cards.properties"; // .. = Roaming
            String canonical = Paths.get(filepath).normalize().toString();           
            return canonical;
        }
        
        throw new IllegalArgumentException("Unknown FileDescriptor: " + fd);
    }
    
    @Override
    public File getFile(FileDescriptor fd)
    {
        String path = getPath(fd);
        return new File(path);
    }
}
