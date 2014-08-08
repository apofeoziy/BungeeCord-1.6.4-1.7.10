package net.md_5.bungee.netty.packetrewriter;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;

public class UpdateTileEntityRewriter extends PacketRewriter {

	@Override
	public void rewriteClientToServer(ByteBuf in, ByteBuf out)
	{
		unsupported( true );
	}

	@Override
	public void rewriteServerToClient(ByteBuf in, ByteBuf out) {
		out.writeBytes(in);
	}

	public void rewriteServerToClient(ByteBuf in, ByteBuf out, Channel ch) {

		UserConnection target = (UserConnection) BungeeCord.getInstance().getPlayer(ch);

		if (target == null || target.getProtocolVersion() < 5) {
			out.writeBytes(in);
		} else {
			out.writeInt(in.readInt());
			out.writeShort(in.readShort());
			out.writeInt(in.readInt());
			byte action = in.readByte();
			out.writeByte(action);

			if (action == 4) {
				try {
					InputStream unzip = new GZIPInputStream(new ByteBufInputStream(in, in.readShort()));
					DataInputStream data = new DataInputStream(unzip); // NBT-Data

					ByteBuf nbtByteBuf = Unpooled.buffer(81);
					DataOutputStream nbtOutput = new DataOutputStream(new GZIPOutputStream(new ByteBufOutputStream(nbtByteBuf)));

					byte type = 10;
					while (type != 0) {
						type = data.readByte();
						nbtOutput.writeByte(type);
						if (type == 0) {
							nbtOutput.writeByte(0); // Write another end-byte to finish
							break;
						}
						String name = data.readUTF();
						nbtOutput.writeUTF(name);

						if (type == 10) {
							continue;
						} else if (name.equals("ExtraType")) {
							nbtOutput.writeUTF(""); // Write Empty String into Tag ExtraType
							data.readUTF();

							byte[] copyBuffer = new byte[16];
							int read;
							while ((read = data.read(copyBuffer)) != -1) {
								nbtOutput.write(copyBuffer, 0, read);
							}
							break;
						} else {
							if (type == 1) {
								nbtOutput.writeByte(data.readByte());
							} else if (type == 3) {
								nbtOutput.writeInt(data.readInt());
							} else if (type == 8) {
								nbtOutput.writeUTF(data.readUTF());
							}
						}
					}
					data.close();
					nbtOutput.close();

					out.writeShort(nbtByteBuf.writerIndex());
					out.writeBytes(nbtByteBuf);
				} catch (IOException e) {
				}
			} else {
				out.writeBytes(in);
			}
		}
	}
}