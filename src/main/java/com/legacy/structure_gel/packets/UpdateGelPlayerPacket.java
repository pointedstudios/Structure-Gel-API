package com.legacy.structure_gel.packets;

import java.util.function.Supplier;

import com.legacy.structure_gel.blocks.GelPortalBlock;
import com.legacy.structure_gel.util.Internal;
import com.legacy.structure_gel.util.capability.GelCapability;
import com.legacy.structure_gel.util.capability.GelCapability.Storage;
import com.legacy.structure_gel.util.capability.GelEntity;
import com.legacy.structure_gel.util.capability.IGelEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class UpdateGelPlayerPacket
{
	protected final IGelEntity gelEntity;

	public UpdateGelPlayerPacket(IGelEntity gelEntity)
	{
		this.gelEntity = gelEntity;
	}

	public static void encoder(UpdateGelPlayerPacket packet, PacketBuffer buff)
	{
		putPortal(buff, packet.gelEntity.getPortal());
		putPortal(buff, packet.gelEntity.getPortalVisual());
		putPortal(buff, packet.gelEntity.getPortalAudio());
	}

	public static UpdateGelPlayerPacket decoder(PacketBuffer buff)
	{
		IGelEntity gelEntity = new GelEntity();
		gelEntity.setPortal(getPortal(buff));
		gelEntity.setPortalVisual(getPortal(buff));
		gelEntity.setPortalAudio(getPortal(buff));
		return new UpdateGelPlayerPacket(gelEntity);
	}

	@Internal
	private static void putPortal(PacketBuffer buff, GelPortalBlock portal)
	{
		buff.writeString(portal != null ? portal.getRegistryName().toString() : Storage.EMPTY.toString());
	}

	@Internal
	private static GelPortalBlock getPortal(PacketBuffer buff)
	{
		ResourceLocation portal = new ResourceLocation(buff.readString());
		return (!portal.equals(Storage.EMPTY) && ForgeRegistries.BLOCKS.containsKey(portal) && ForgeRegistries.BLOCKS.getValue(portal) instanceof GelPortalBlock) ? (GelPortalBlock) ForgeRegistries.BLOCKS.getValue(portal) : null;
	}

	public static void handler(UpdateGelPlayerPacket packet, Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(packet)));
		context.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handlePacket(UpdateGelPlayerPacket packet)
	{
		Minecraft mc = Minecraft.getInstance();
		GelCapability.ifPresent(mc.player, (ge) ->
		{
			ge.setPortal(packet.gelEntity.getPortal());
			ge.setPortalVisual(packet.gelEntity.getPortalVisual());
			ge.setPortalAudio(packet.gelEntity.getPortalAudio());
		});
	}
}