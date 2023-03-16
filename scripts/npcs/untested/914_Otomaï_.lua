local npc = Npc(914, 9066)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4058, {3543, 3532, 3577, 353})
    elseif answer == 3543 then p:ask(4069, {534})
    elseif answer == 3577 then p:ask(4104, {3795, 369})
    elseif answer == 3532 then p:ask(4067, {356})
    end
end

RegisterNPCDef(npc)