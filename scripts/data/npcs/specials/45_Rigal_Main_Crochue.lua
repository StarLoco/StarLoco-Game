local npc = Npc(45, 9023)
--TODO: NPC Temple Sram > Check dialogs offi en étant Sram
npc.colors = {0x6b7498, -1, 0x570828}
npc.sales = {
    {item=787},
    {item=1327},
    {item=495}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(98, {153})
    elseif answer == 153 then p:ask(185, {158})
    elseif answer == 158 then p:ask(191)
    end
end

RegisterNPCDef(npc)
