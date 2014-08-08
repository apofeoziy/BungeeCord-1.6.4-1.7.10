package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class WindowItemsRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        out.writeByte( in.readByte() );
        short size = in.readShort();
        out.writeShort( size );
        for ( int i = 0; i < size; i++ )
        {
            Var.rewriteItemData( in, out );
        }
    }

}
