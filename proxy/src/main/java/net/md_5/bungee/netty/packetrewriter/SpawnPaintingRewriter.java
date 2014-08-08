package net.md_5.bungee.netty.packetrewriter;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.netty.Var;

public class SpawnPaintingRewriter extends PacketRewriter
{

    @Override
    public void rewriteClientToServer(ByteBuf in, ByteBuf out)
    {
        unsupported( true );
    }

    @Override
    public void rewriteServerToClient(ByteBuf in, ByteBuf out)
    {
        int entityId = in.readInt();
        String title = Var.readString( in, false );
        Var.writeVarInt( entityId, out );
        Var.writeString( title, out, true );
        out.writeBytes( in.readBytes( in.readableBytes() ) );
    }

}
