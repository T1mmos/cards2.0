package gent.timdemey.cards.services.interfaces;

import java.io.File;

import gent.timdemey.cards.services.contract.descriptors.FileDescriptor;

public interface IFileService
{
    public File getFile(FileDescriptor fd);
}
