package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;

public abstract class PacketRewriter
{

    public abstract void rewriteClientToServer(ByteBuf in, ByteBuf out);
    public abstract void rewriteServerToClient(ByteBuf in, ByteBuf out);


    public void writeString(String s, ByteBuf buf)
    {
        // TODO: Check len - use Guava?
        buf.writeShort( s.length() );
        for ( char c : s.toCharArray() )
        {
            buf.writeChar( c );
        }
    }

    public String readString(ByteBuf buf)
    {
        // TODO: Check len - use Guava?
        short len = buf.readShort();
        char[] chars = new char[ len ];
        for ( int i = 0; i < len; i++ )
        {
            chars[i] = buf.readChar();
        }
        return new String( chars );
    }

    public void unsupported(boolean clientside) throws UnsupportedOperationException
    {
        if ( clientside )
        {
            throw new UnsupportedOperationException( "This packet is only client to server.");
        } else
        {
            throw new UnsupportedOperationException( "This packet is only server to client." );
        }
    }
}
