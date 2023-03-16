local npc = Npc(622, 120)

npc.colors = {8149872, 16777215, 7361640}
npc.accessories = {0, 6958, 0, 0, 0}

npc.sales = {
    {item=7042}
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2501, {2151, 2147})
    elseif answer == 2147 then p:ask(2502, {2148})
    elseif answer == 2148 then p:ask(2503, {2150})
    elseif answer == 2150 then p:endDialog()
    elseif answer == 2151 then p:ask(2506)
    end
end

RegisterNPCDef(npc)
